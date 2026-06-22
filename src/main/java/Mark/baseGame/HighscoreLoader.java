package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class HighscoreLoader
{

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private List<ScoreEntry> highscoreList;
    private String userHome = System.getProperty("user.home");
    private Path saveDirectory = Path.of(userHome, Settings.getSaveDirectoryName());
    private final Path LOCAL_FILE = saveDirectory.resolve(Settings.getLocalHighscoreFileName());
    private final Path METADATA_FILE = saveDirectory.resolve(Settings.getMetadataFileName());
    private boolean isFileDirty = false;
    private boolean fileCorruptedAndRestored = false;
    private String lastSyncTimestamp = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyUnknown());
    HighscoreLoader(List<ScoreEntry> highscoreList)
    {
        this.highscoreList = highscoreList;
    }

    public void load()
    {
        load(()->{});
    }

    public void load(Runnable onComplete)
    {
        loadLocalFile();
        fetchHighscores(list ->{
            if (onComplete != null) onComplete.run();
        });
    }
    public void  safe(String name, int score, int placed, Runnable onComplete)
    {
        String cleanedName = name.trim();
        saveLocalAndRefresh(cleanedName,score,false);
        sendSingleScoreOnline(cleanedName,score,placed, onComplete);
    }

    public void fetchHighscores(java.util.function.Consumer<List<ScoreEntry>> onComplete)
    {
        loadLocalFile();

        try
        {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Settings.getHighscoreServerUrl() + Settings.getApiActionGet())).timeout(Duration.ofSeconds(Settings.getHttpTimeoutSeconds())).GET().build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(stringHttpResponse ->{
                if (stringHttpResponse.statusCode() == HttpURLConnection.HTTP_OK && !stringHttpResponse.body().trim().isEmpty())
                {
                    Platform.runLater(() -> {
                      this.isFileDirty = false;

                      List<ScoreEntry> unsyncedLocalScores = new ArrayList<>();
                      for (ScoreEntry entry : highscoreList)
                      {
                          if (!entry.isSynced()) unsyncedLocalScores.add(entry);
                      }

                      List<ScoreEntry> onlineList = new ArrayList<>();
                      String[] lines = stringHttpResponse.body().split("\n");
                      for (String line : lines)
                      {
                          String[] parts = line.split(Settings.getDelimiterScoreEntry());
                          if (parts.length == 2)
                          {
                              ScoreEntry oe = new ScoreEntry(parts[0], Integer.parseInt(parts[1].trim()), 0);
                              oe.setSynced(true);
                              onlineList.add(oe);
                          }
                      }
                      highscoreList.clear();
                      highscoreList.addAll(onlineList);
                      highscoreList.addAll(unsyncedLocalScores);
                      highscoreList.sort((a,b)-> Integer.compare(b.getScore(), a.getScore()));

                      for (int i = 0; i < highscoreList.size(); i++) highscoreList.get(i).setPlace(i+1);

                      saveEntireListLocally();
                      saveSyncTimestamp();
                      syncOfflineScores();
                      if (onComplete != null) onComplete.accept(new ArrayList<>(highscoreList));
                    });
                }
                else
                {
                    Platform.runLater(()->{
                      if (onComplete != null) onComplete.accept(new ArrayList<>(highscoreList));
                    });
                }
            }).exceptionally(ex -> {
                Platform.runLater(() -> {
                  System.err.println(Settings.getErrMsgServerTimeout());
                  if (onComplete != null) onComplete.accept(new ArrayList<>(highscoreList));
                });
                return null;
            });
        }
        catch (Exception e)
        {
            Platform.runLater(() -> {
              if (onComplete != null) onComplete.accept(new ArrayList<>(highscoreList));
            });
        }
    }

    private void sendSingleScoreOnline(String name, int score, int placed)
    {
        sendSingleScoreOnline(name,score,placed,()->{});
    }

    private void sendSingleScoreOnline(String name, int score, int placed, Runnable onComplete)
    {

        try
        {
            String hash = generateSHA256(Settings.getHighscorePhpPwd() + name + score);
            String postData = String.format(Settings.getFormatApiSave(), name, score, placed, hash);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Settings.getHighscoreServerUrl()))
                    .timeout(Duration.ofSeconds(Settings.getHttpTimeoutSeconds()))
                    .header(Settings.getHttpHeaderContentType(), Settings.getHttpHeaderUrlEncoded())
                    .POST(HttpRequest.BodyPublishers.ofString(postData, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(stringHttpResponse -> {
               Platform.runLater(()->{
                   if (stringHttpResponse.body().trim().equals(Settings.getServerResponseSuccess()))
                   {
                       markScoreAsSynced(name,score);
                       saveSyncTimestamp();
                   }
                   else
                   {
                       System.err.println(Settings.getErrMsgServerError() + stringHttpResponse.body());
                   }
                   if (onComplete != null) onComplete.run();
               });
            }).exceptionally(ex ->{

                Platform.runLater(()-> {
                  System.err.println(Settings.getErrMsgServerOffline());
                  if (onComplete != null) onComplete.run();
                });
                return null;
            });
        }
        catch (Exception e)
        {
            System.err.println(Settings.getErrMsgDirectSaveFailed() + e.getMessage());
            if (onComplete != null) onComplete.run();
        }

    }

    private void syncOfflineScores()
    {
        for (ScoreEntry entry : new ArrayList<>(highscoreList))
        {
            if (!entry.isSynced())
            {
                sendSingleScoreOnline(entry.getName(),entry.getScore(),0);
            }
        }
    }

    private void loadLocalFile()
    {
        ensureDirectoryExists();
        highscoreList.clear();
        if (!Files.exists(LOCAL_FILE))
        {
            initDummyHighscore();
            saveEntireListLocally();
            return;
        }

        try
        {
            List<String> lines = Files.readAllLines(LOCAL_FILE);
            if (lines.isEmpty()) return;

            String lastLine = lines.get(lines.size() -1);
            if (!lastLine.startsWith(Settings.getPrefixHash()))
            {
                System.out.println(Settings.getErrMsgNoHash());
                initDummyHighscore();
                return;
            }
            String safedHash = lastLine.substring(Settings.getPrefixHash().length());
            List<String> decryptedLines = new ArrayList<>();
            StringBuilder dataForHash = new StringBuilder();
            for (int i = 0 ; i < lines.size() -1; i++)
            {
                String decryptedLine = decrypt(lines.get(i));
                decryptedLines.add(decryptedLine);
                dataForHash.append(decryptedLine);
            }
            String controllHash = generateSHA256(dataForHash.toString() + Settings.getHighscorePhpPwd());
            if (!controllHash.equals(safedHash))
            {
                System.out.println(Settings.getErrMsgDataManipulated());
                this.fileCorruptedAndRestored = true;
                loadSyncTimestamp();
                this.isFileDirty = true;
                initDummyHighscore();
                saveEntireListLocally();
                return;
            }

            this.isFileDirty = false;

            int currentPlace = 1;
            for (int i = 0 ; i < decryptedLines.size();i++)
            {
                String[] parts = decryptedLines.get(i).split(Settings.getDelimiterScoreEntry());
                if (parts.length >= 3)
                {
                    ScoreEntry entry = new ScoreEntry(parts[0], Integer.parseInt(parts[1].trim()),currentPlace);
                    entry.setSynced(Boolean.parseBoolean(parts[2].trim()));
                    highscoreList.add(entry);
                    currentPlace++;
                }
            }
        }
        catch (Exception e)
        {
            System.err.println(Settings.getErrMsgFileCorrupted());
            this.fileCorruptedAndRestored = true;
            loadSyncTimestamp();
            initDummyHighscore();
            saveEntireListLocally();
        }

    }

    private void saveLocalAndRefresh(String name, int score, boolean isSynced)
    {

        ScoreEntry newEntry = new ScoreEntry(name,score,0);
        newEntry.setSynced(isSynced);

        highscoreList.add(newEntry);
        highscoreList.sort((a,b)->Integer.compare(b.getScore(), a.getScore()));

        for (int i = 0; i < highscoreList.size(); i++)
        {
            highscoreList.get(i).setPlace(i+1);
        }

        saveEntireListLocally();

    }

    private void saveEntireListLocally()
    {
        ensureDirectoryExists();
        try
        {
            List<String> linesToSave = new ArrayList<>();
            StringBuilder dataForHash = new StringBuilder();
            for (ScoreEntry entry : highscoreList)
            {
                String plainLine = entry.getName() + Settings.getDelimiterScoreEntry()
                        + entry.getScore() + Settings.getDelimiterScoreEntry()
                        + entry.isSynced();
                dataForHash.append(plainLine);
                String encryptedLine = encrypt(plainLine);
                linesToSave.add(encryptedLine);
            }
            String controllHash = generateSHA256(dataForHash.toString() + Settings.getHighscorePhpPwd());
            linesToSave.add(Settings.getPrefixHash() + controllHash);
            Files.write(LOCAL_FILE, linesToSave);
        }
        catch (IOException e)
        {
            System.err.println(Settings.getErrMsgSaveLocalFailed() +e.getMessage());
        } catch (Exception e) {
            System.err.println(Settings.getErrMsgEncryptFailed() + e.getMessage());
        }
    }

    private void markScoreAsSynced(String name, int score)
    {
        for (ScoreEntry entry : new ArrayList<>(highscoreList))
        {
            if (entry.getName().equals(name) && entry.getScore() == score && !entry.isSynced())
            {
                entry.setSynced(true);
                break;
            }
        }
        saveEntireListLocally();
    }

    public void pingServer(java.util.function.Consumer<Boolean> onResult)
    {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Settings.getHighscoreServerUrl() + Settings.getApiActionPing()))
                    .timeout(Duration.ofSeconds(Settings.getPingTimeoutSeconds()))
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> {
                boolean reachable = (res.statusCode() == HttpURLConnection.HTTP_OK);
                Platform.runLater(() -> onResult.accept(reachable));
            }).exceptionally(ex -> {
                Platform.runLater(() -> onResult.accept(false));
                return null;
            });
        }
        catch (Exception e)
        {
            Platform.runLater(()-> onResult.accept(false));
        }
    }

    private void initDummyHighscore()
    {
        highscoreList.clear();
        String[] names = Settings.getDummyNames();
        int[] scores = Settings.getDummyScores();

        for (int i = 0; i < names.length; i++) {
            ScoreEntry dummy = new ScoreEntry(names[i], scores[i], 0);
            dummy.setSynced(true);
            highscoreList.add(dummy);
        }

        highscoreList.sort((a,b) -> Integer.compare(b.getScore(), a.getScore()));

        for (int i = 0; i < highscoreList.size(); i++) highscoreList.get(i).setPlace(i + 1);

    }

    private void ensureDirectoryExists()
    {
        try {
            if (!Files.exists(saveDirectory))
            {
                Files.createDirectories(saveDirectory);
            }
        }catch (IOException e)
        {
            System.err.println(Settings.getErrMsgCreateDirFailed() + e.getMessage());
        }
    }

    private void saveSyncTimestamp()
    {
        try
        {
            ensureDirectoryExists();
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(Settings.getFormatDateTimeSync()));
            String encryptedTimestamp = encrypt(now);
            Files.writeString(METADATA_FILE,encryptedTimestamp);
        }
        catch (Exception e)
        {
            System.err.println(Settings.getErrMsgSaveSyncTimeFailed() + e.getMessage());
        }
    }

    private void loadSyncTimestamp()
    {
        try
        {
            if (Files.exists(METADATA_FILE))
            {
                String encryptedContent = Files.readString(METADATA_FILE).trim();
                this.lastSyncTimestamp = decrypt(encryptedContent);
            }
            else
            {
                this.lastSyncTimestamp = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyUnknown());
            }
        }
        catch (Exception e)
        {
            this.lastSyncTimestamp = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyUnknown());
        }
    }

    public boolean isFileCorruptedAndRestored()
    {
        return fileCorruptedAndRestored;
    }

    public String getLastSyncTimestamp()
    {
        return lastSyncTimestamp;
    }

    private String generateSHA256(String input) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance(Settings.getCryptoAlgoSha256());
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes)
        {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append("0");
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private String encrypt(String plainText) throws Exception
    {
        byte[] keyBytes = Settings.getHighscoreSecretKey().getBytes(StandardCharsets.UTF_8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, Settings.getCryptoAlgoAes());
        Cipher cipher = Cipher.getInstance(Settings.getCryptoAlgoAes());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decrypt(String encryptedText) throws Exception
    {
        byte[] keyBytes = Settings.getHighscoreSecretKey().getBytes(StandardCharsets.UTF_8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, Settings.getCryptoAlgoAes());
        Cipher cipher = Cipher.getInstance(Settings.getCryptoAlgoAes());
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes,StandardCharsets.UTF_8);
    }

}
