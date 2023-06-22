package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class Checkers {

    public static final List<IChecker> checkerList = makeCheckerList();

    private static void addGenericRatChecker(List<IChecker> list) {
        final HashMap<String, TestResult.TestResultLevel> susMap = new HashMap<>();
        final HashMap<Pattern, TestResult.TestResultLevel> susPatternMap = new HashMap<>();
        susPatternMap.put(Pattern.compile("/users/@me/billing"), TestResult.TestResultLevel.STRONG_SUS);
        susPatternMap.put(Pattern.compile("AppData"), TestResult.TestResultLevel.SUS);
        susPatternMap.put(Pattern.compile("Application Support"), TestResult.TestResultLevel.SUS);
        susPatternMap.put(Pattern.compile("Default[/|\\\\](Login Data|Local Storage|Web Data|Cookies|Network)"), TestResult.TestResultLevel.STRONG_SUS);
        susPatternMap.put(Pattern.compile("User Data[/|\\\\](Local State|Default)"), TestResult.TestResultLevel.STRONG_SUS);
        susPatternMap.put(Pattern.compile("Local Storage[/|\\\\]leveldb"), TestResult.TestResultLevel.STRONG_SUS);
        // Regex from https://github.com/MinnDevelopment/discord-webhooks/blob/bbbd1e0a7ff1bdeef64df3d7a769105e118a60af/src/main/java/club/minnced/discord/webhook/WebhookClientBuilder.java#L46
        susPatternMap.put(Pattern.compile("(?:https?://)?(?:\\w+\\.)?discord(?:app)?\\.com/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?"), TestResult.TestResultLevel.SUS);
        final StringChecker susTest = new StringChecker("Possible RAT / stealer", susMap, susPatternMap);
        list.add(susTest);
    }

    private static void addYoinkRatChecker(List<IChecker> list) {
        final HashMap<String, TestResult.TestResultLevel> susMap = new HashMap<>();
        final String[] virusStrings = {
            // Moneyrat
            ".apiloader.APILoader",
            "net.minecraftforge:apiloader:1.0.4",
            " --tweakClass net.minecraftforge.apiloader.APILoader",
            "net.minecraftforge.apiloader.APILoader",
            "libraries/net/minecraftforge/apiloader/1.0.4/apiloader-1.0.4.jar",
            "APILoader - 28 Dec 2020\n\n",
            // v1
            "--tweakClass net.minecraftforge.apiloader.APILoader",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvNzk5MDk5MzU4MDg3MzQ4MzE1L1VyTjQtV2psTDBsQUJNckF3dVpiMUdUUDJkUFdJbG1jR0JEbmJKalFYMmhwdE4taF8xXzBfeUxROXlSR0JNeGc4X0dK",
            "http://yoink.site/ukraine/rat.jar",
            "the rat is running out of the game",
            "https://cdn-107.anonfiles.com/xe9fG219y7/1a2d8176-1659361918/build.exe",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvMTAwODQzNTUwNzc5MDg5MzA2Ny9GTjVoYmhjWkJNY183ZnVrR1MyX3JjQnFWWkc3dDhGaURlZlBrVGpIZVRkRFhrU0liWEh6Z3plQmhpcWd1SUVUV0QwRw==",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvODMwNTQ5ODg5NzgzNTYyMjcwL193OW8zQlgyck1zNzl2ZmMtdGNWWm9BZlYtLTVxQXJDdFFhbEJBZ09VMzdHZ3J3SENyLWgzdXVLVmVZeWJnY3g1N20t",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvODMwNTgwNjUzNjA2Njk5MDIwLzROaFBRcWlhY3NKREFOYnZ5YlFxMFZDZTJWOE5OaldwLUl1SXR6cF96QVZzZG54SVRwWUdIbkF5LVhHb1Rqb29vc0Zo",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvODMwNTgwNzczNTM0MzAyMjE5L1ZLd2dlUzh0NnQtTlJ2ZTFGN19HOTdGR29FQUxIdnoxb3VrbmdhU2lVV2dPYThGalZCbE4xS2dnZEVhbFJWYlRVNW9V",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvODMwNTgwODY1NjQxMDg3MDI3Ly1MN21lWFlJZDJTVHVYbVhsYmFoczd1ekFJTVEyQWlBU2NyQUIwbVVQbFI1dHJlOVF0dUdHYUhFUm02bFpVUEU1bjQy",
            "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvODMwNTgwOTczMDE5NDYzNzMxL0UteUNKTnF1ODhrT2g4VDZxdVJHbEFMYmR0ZTFLSW5LMU9uZkw2cS1rZklSM2dWSks3WXR0WXZYQ2xfVVFaa3NpLUNS",
        };

        for (final String virusString : virusStrings) {
            susMap.put(virusString, TestResult.TestResultLevel.VIRUS);
        }

        final String[] strongSusStrings = {
            // Moneyrat
            "/.metadata/.plugins/org.eclipse.buildship.ui/dialog_settings.xml",
            "/AppData/Local/BraveSoftware/Brave-Browser/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/Google/Chrome/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/Microsoft/Edge/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/Vivaldi/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/Yandex/YandexBrowser/User Data/Default/Local Storage/leveldb/",
            "/AppData/Roaming/BraveSoftware/Brave-Browser/User Data/Default/Local Storage/leveldb/",
            "/AppData/Roaming/discord/Local Storage/leveldb/",
            "/AppData/Roaming/discordcanary/Local Storage/leveldb/",
            "/AppData/Roaming/discordptb/Local Storage/leveldb/",
            "/AppData/Roaming/FileZilla/recentservers.xml",
            "/AppData/Roaming/JetBrains/",
            "/AppData/Roaming/Mozilla/Firefox/Profiles/",
            "/AppData/Roaming/Opera Software/Opera Stable/Local Storage/leveldb/",
            "/AppData/Roaming/Yandex/YandexBrowser/User Data/Default/Local Storage/leveldb/",
            "/eclipse/configuration/.settings/org.eclipse.ui.ide.prefs",
            "/Library/Application Support/discord/Local Storage/leveldb/",
            "/Library/Application Support/discordcanary/Local Storage/leveldb/",
            "/Library/Application Support/discordptb/Local Storage/leveldb/",
            "/Library/Application Support/Firefox/Profiles/",
            "/Library/Application Support/Google/Chrome/User Data/Default/Local Storage/leveldb/",
            "/options/recentProjects.xml",
            "\nAdditional discord data:\n",
            //"\nDesktop folder:\n"
            "\nDiscord token[s]: \n",
            //"\nDownloads folder:\n"
            "\nEclipse workspaces:\n",
            "\nFileZilla hosts:\n",
            "\nFuture accounts: \n",
            "\nFuture loader credentials:\n",
            "\nFuture waypoints: \n",
            "\nIntellij workspaces:\n",
            "\nJourneyMap waypoints: \n",
            "\nKAMI Blue waypoints: \n",
            "\nMinecraft data: \n",
            //"\nMinecraft mods:\n"
            "\nMultiMC accounts.json: \n",
            "\nPyro accounts:\n",
            "\nPyro loader credentials: \n",
            "\nPyro waypoints:\n",
            //"\nRunning processes:\n"
            "\nRusherHack accounts:\n",
            "\nRusherHack loader credentials: \n",
            "\nRusherHack waypoints:\n",
            //"\nuser.home:\n"
            "<item key=\"project_location\" ",
            "<option name=\"recentPaths\">",
            "428A487E3361EF9C5FC20233485EA236",
            "89D85BE00F56ACE593BC029C686E9BA5",
            "99CE85B34778C8C765CD2F222748EF11",
            "F6DA144461738529DB35B7DC4E2578B2",
            "http://dengimod.cf/discordhook/sendstuff2.php",
            "https://pastebin.com/raw/eiv5znvZ",
            "https://pastebin.com/raw/X5UHFxtM",
            "ps -e -o command",
            "rusherhack/alts.json",
            "rusherhack/waypoints.json",
            "wmic process get name,executablepath",
            // V1
            "[\\w\\W]{24}\\.[\\w\\W]{6}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}",
            "\\BraveSoftware\\Brave-Browser\\User Data\\Default",
            "\\Future\\accounts.txt",
            "\\Google\\Chrome\\User Data\\Default",
            "\\Google\\Chrome\\User Data\\Default\\Login Data",
            "\\LightCord",
            "\\Local Storage\\leveldb\\",
            "\\Microsoft\\Edge\\User Data\\Default",
            "\\Mozilla\\Firefox\\Profiles",
            "\\Opera Software\\Opera Stable",
            "\\Yandex\\YandexBrowser\\User Data\\Default",
            "\u0024USER_HOME\u0024",
            "https://pastebin.com/raw/jdiVNVZ2",
            "https://pastebin.com/raw/ZrMLRRar",
            // 1.5
            "/.config/BraveSoftware/Brave-Browser/Default/Login Data",
            "/.config/BraveSoftware/Brave-Browser/Local State",
            "/.config/google-chrome/Default/Login Data",
            "/.config/google-chrome/Local State",
            "/.config/Microsoft/Edge/Default/Login Data",
            "/.config/Microsoft/Edge/Local State",
            "/.config/opera/Default/Login Data",
            "/.config/opera/Local State",
            "/.config/Yandex/YandexBrowser/Default/Login Data",
            "/.config/Yandex/YandexBrowser/Local State",
            "/BraveSoftware/Brave-Browser/Default/Login Data",
            "/BraveSoftware/Brave-Browser/Local State",
            "/Google/Chrome/User Data/Default/Login Data",
            "/Google/Chrome/User Data/Local State",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Login Data",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Local State",
            "/Library/Application Support/Google/Chrome/User Data/Default/Login Data",
            "/Library/Application Support/Google/Chrome/User Data/Local State",
            "/Library/Application Support/Microsoft/Edge/Default/Login Data",
            "/Library/Application Support/Microsoft/Edge/Local State",
            "/Library/Application Support/Opera/Opera/Default/Login Data",
            "/Library/Application Support/Opera/Opera/Local State",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Login Data",
            "/Library/Application Support/Yandex/YandexBrowser/Local State",
            "/Microsoft/Edge/User Data/Default/Login Data",
            "/Microsoft/Edge/User Data/Local State",
            "/Opera Software/Opera Stable/Default/Login Data",
            "/Opera Software/Opera Stable/Local State",
            "/Yandex/YandexBrowser/User Data/Default/Login Data",
            "/Yandex/YandexBrowser/User Data/Local State",
            "SELECT `origin_url`,`username_value`,`password_value` from `logins`",
            // later
            "/.config/BraveSoftware/Brave-Browser/Default/Cookies",
            "/.config/BraveSoftware/Brave-Browser/Default/Network/Cookies",
            "/.config/BraveSoftware/Brave-Browser/Default/Web Data",
            "/.config/google-chrome/Default/Cookies",
            "/.config/google-chrome/Default/Network/Cookies",
            "/.config/google-chrome/Default/Web Data",
            "/.config/Microsoft/Edge/Default/Cookies",
            "/.config/Microsoft/Edge/Default/Network/Cookies",
            "/.config/Microsoft/Edge/Default/Web Data",
            "/.config/opera/Default/Cookies",
            "/.config/opera/Default/Network/Cookies",
            "/.config/opera/Default/Web Data",
            "/.config/Yandex/YandexBrowser/Default/Cookies",
            "/.config/Yandex/YandexBrowser/Default/Network/Cookies",
            "/.config/Yandex/YandexBrowser/Default/Web Data",
            "/BraveSoftware/Brave-Browser/Default/Cookies",
            "/BraveSoftware/Brave-Browser/Default/Network/Cookies",
            "/BraveSoftware/Brave-Browser/Default/Web Data",
            "/Google/Chrome/User Data",
            "/Google/Chrome/User Data/Default/Cookies",
            "/Google/Chrome/User Data/Default/Network/Cookies",
            "/Google/Chrome/User Data/Default/Web Data",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Cookies",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Network/Cookies",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Web Data",
            "/Library/Application Support/Google/Chrome/User Data/Default/Cookies",
            "/Library/Application Support/Google/Chrome/User Data/Default/Network/Cookies",
            "/Library/Application Support/Google/Chrome/User Data/Default/Web Data",
            "/Library/Application Support/Microsoft/Edge/Default/Cookies",
            "/Library/Application Support/Microsoft/Edge/Default/Network/Cookies",
            "/Library/Application Support/Microsoft/Edge/Default/Web Data",
            "/Library/Application Support/Opera/Opera/Default/Cookies",
            "/Library/Application Support/Opera/Opera/Default/Network/Cookies",
            "/Library/Application Support/Opera/Opera/Default/Web Data",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Cookies",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Network/Cookies",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Web Data",
            "/Microsoft/Edge/User Data/Default/Cookies",
            "/Microsoft/Edge/User Data/Default/Network/Cookies",
            "/Microsoft/Edge/User Data/Default/Web Data",
            "/Opera Software/Opera Stable/Default/Cookies",
            "/Opera Software/Opera Stable/Default/Network/Cookies",
            "/Opera Software/Opera Stable/Default/Web Data",
            "/Yandex/YandexBrowser/User Data/Default/Cookies",
            "/Yandex/YandexBrowser/User Data/Default/Network/Cookies",
            "/Yandex/YandexBrowser/User Data/Default/Web Data",
            "\\recentservers.xml",
            "=========[CRYPTO]=========\n",
            "=========[DISCORD INFO]=========\n",
            "=========[MINECRAFT]=========\n",
            "=========[OTHER]=========\n",
            "=========[PASSWORDS]=========\n",
            "=========[STEAM]=========\n",
            "=========[SYSTEM INFO]=========\n",
            "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvdjYvdXNlcnMvQG1lL2JpbGxpbmcvcGF5bWVudC1zb3VyY2Vz",
            "Browser Autofill\n",
            "Browser Cookies\n",
            "Browser Credit Cards\n",
            "com.liberty.jaxx\\IndexedDB\\file__0.indexeddb.leveldb\\",
            "config\\loginusers.vdf",
            "FileZilla/recentservers.xml",
            "FROM: %s\nURL: %s\nUSERNAME: %s\nPASSWORD: %s\n------------\n",
            "HKCU\\Software\\Bitcoin\\Bitcoin-Qt",
            "HKCU\\Software\\Dash\\Dash-Qt",
            "HKCU\\Software\\Litecoin\\Litecoin-Qt",
            "HKCU\\Software\\monero-project\\monero-core",
            "HKLM\\SOFTWARE\\WOW6432Node\\Valve\\Steam",
            "HOST KEY: %s\nNAME: %s\nPATH: %s\nEXPIRES (UTC): %s\nVALUE: %s\n------------\n",
            "http://yoink.site/atlanta/%s.php",
            "manatee.technology 1.5 | by juggenbande",
            "NAME: %s\nDATE: %s/%s\nCARD: %s\n------------\n",
            "NAME: %s\nVALUE: %s\nCREATED: %s\nLAST USED: %s\nCOUNT: %s\n------------\n",
            "SELECT * from `credit_cards`",
            "SELECT `host_key`,`name`,`path`,`encrypted_value`,`expires_utc` from `cookies`",
            "SELECT date_created,date_last_used,name,value,count from `autofill` ORDER BY date_created",
            "Steam/config/",
            "Telegram Desktop\\tdata",

            "tasklist.exe",
            "wireshark",
            "com.qqTechnologies.qqbackdoor.MainClass",
            "com.qqTechnologies.qqbackdoor",
            "justice4qq",
            "--tweakClass net.minecraftforge.coremod.FMLCoremodTweaker",
            " --tweakClass net.minecraftforge.coremod.FMLCoremodTweaker",
            "net.minecraftforge:coremod:1.0.12",
            "libraries/net/minecraftforge/coremod/1.0.12",
            "libraries/net/minecraftforge/coremod/1.0.12/coremod-1.0.12.jar",

            "/.config/discord/Cache/Local Storage/leveldb/",
            "/.config/discordcanary/Cache/Local Storage/leveldb/",
            "/.config/discordptb/Cache/Local Storage/leveldb/",
            "/AppData/Local/Google/Chrome/User Data/",
            "C:\\Users\\",

            "[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}",
            "mfa\\.[\\w-]{84}",
            "302094807046684672",

            "https://discordapp.com/api/v6/users/@me/billing/payment-sources",
        };

        for (final String susString : strongSusStrings) {
            susMap.put(susString, TestResult.TestResultLevel.STRONG_SUS);
        }

        final String[] susStrings = {
            ".wallet",
            "[\\w\\.]{24}\\.[\\w\\.]{6}\\.[\\w\\.\\-]{27}|mfa\\.[\\w\\.\\-]{84}",
            "[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}",
            "@everyone NEW LOG ",
            "/Future/accounts.txt",
            "/Future/auth_key",
            "/Future/waypoints.txt",
            "\"76(.*?)\"",
            "\\.minecraft\\Pyro\\server",
            "\\.minecraft\\SalHack\\Waypoints\\Waypoints.json",
            "\\Documents\\ShareX\\",
            "\\Future\\backup",
            "\\wallet.dat",
            "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvdjYvdXNlcnMvQG1l",
            "Armory\\",
            "atomic\\Local Storage\\leveldb\\",
            "bytecoin\\",
            "cHJlbWl1bV90eXBl",
            "Crypto/Armory",
            "Crypto/AtomicWallet",
            "Crypto/BitcoinCore",
            "Crypto/Bytecoin",
            "Crypto/DashCore",
            "Crypto/Electrum",
            "Crypto/Ethereum",
            "Crypto/Exodus",
            "Crypto/Jaxx",
            "Crypto/LitecoinCore",
            "Crypto/MoneroCore",
            "Crypto/Zcash",
            "dQw4w9WgXcQ:[^\"]*",
            "Electrum\\wallets\\",
            "Ethereum\\keystore\\",
            "Exodus\\exodus.wallet\\",
            "https://cdn.discordapp.com/attachments/761105850194329600/765200019488899102/5ccabf62108d5a8074ddd95af2211727.png",
            "https://cdn.discordapp.com/avatars/703469635416096839/a_fdaa18602fc0a9b5ce3577a54d2ca262.webp",
            "https://discordapp.com/api/v7/invites/minecraft",
            "https://steamcommunity.com/profiles/",
            "KAMIBlueWaypoints.json",
            "Pyro/alts.json",
            "Pyro/launcher.json",
            "Pyro/server/",
            "Pyro\\alts.json",
            "rusherhack\\alts.json",
            "rusherhack\\waypoints.json",
            "strDataDir",
            "wallet_path",
            "Zcash\\",
        };

        for (final String susString : susStrings) {
            susMap.put(susString, TestResult.TestResultLevel.SUS);
        }

        final String[] begignStrings = {
            // Netty
            "\\AppData\\Local\\Temp",
            "http://checkip.amazonaws.com",
            "https://wtfismyip.com/text",
            //"launcher_profiles.json"
            //"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.11 Safari/537.36"
            //"https://discordapp.com/api/v6/users/@me",
            "https://discordapp.com/api/v6/users/@me",
            "Failed to get future auth ",
            "\\discordcanary",
            "\\discordptb",
            "webappsstore",
            "\\.minecraft\\journeymap",
            "(?<=\\G.{1900})",
            "\u0043\u0072\u0065\u0061\u0074\u0065\u0064\u0020\u0062\u0079\u0020\u0079\u006f\u0069\u006e\u006b",
            "dQw4w9WgXcQ:"
        };

        for (final String begignString : begignStrings) {
            susMap.put(begignString, TestResult.TestResultLevel.BENIGN);
        }

        final StringChecker susTest = new StringChecker("YoinkRat", susMap);
        list.add(susTest);
    }

    private static void addSkyrageChecker(List<IChecker> list) {
        final HashMap<String, TestResult.TestResultLevel> susMap = new HashMap<>();
        final String[] virusStrings = {
            "kernel-certs-debug4917.log",
            "KguQvFBPWsHhudivS2ccfiTv7lwzMtqzpFJdWRhxkaU=",
            "KguQvFBPWsHhudivS2ccfpkWIGgJLbt3",
            "KguQvFBPWsHhudivS2ccfrWv1IgzKb9r5vfjM4Vlj8A=",
            "first.throwable.in",
            "t23e7v6uz8idz87ehugwq.skyrage.de",
            "http://files.skyrage.de/mvd",
            "aHR0cDovL2ZpbGVzLnNreXJhZ2UuZGUvdXBkYXRl",
            "aHR0cDovL2ZpbGVzLnNreXJhZ2UuZGUvdXBkYXR",
            "http://files.skyrage.de/update",
            "http://first.throwable.in/update",
            "http://first.throwable.in/mvd",
        };

        for (final String virusString : virusStrings) {
            susMap.put(virusString, TestResult.TestResultLevel.VIRUS);
        }

        susMap.put("/plugi", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("n-config.bin", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("plugin-config.bin", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("/plugin-config.bin", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("REPLACE HEREEEE", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("LWphc", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("LWphcg", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("-Dgnu=", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("/bin/java", TestResult.TestResultLevel.SUS);
        susMap.put("\\bin\\javaw.exe", TestResult.TestResultLevel.SUS);
        susMap.put("java.io.tmpdir", TestResult.TestResultLevel.BENIGN);
        final HashMap<Pattern, TestResult.TestResultLevel> susPatternMap = new HashMap<>();
        susPatternMap.put(Pattern.compile("first\\.throwable\\.in"), TestResult.TestResultLevel.VIRUS);
        susPatternMap.put(Pattern.compile("skyrage\\.de"), TestResult.TestResultLevel.VIRUS);
        final StringChecker susTest = new StringChecker("Skyrage", susMap, susPatternMap);
        list.add(susTest);
    }

    private static void addNekoClientChecker(List<IChecker> list) {
        // WIP, doesn't detect anything but the two known weird strings in infected mods
        final HashMap<String, TestResult.TestResultLevel> susMap = new HashMap<>();
        susMap.put("dos:hidden", TestResult.TestResultLevel.BENIGN);
        susMap.put("dos:system", TestResult.TestResultLevel.BENIGN);
        susMap.put("run.bat", TestResult.TestResultLevel.BENIGN);
        susMap.put("System32", TestResult.TestResultLevel.BENIGN);
        susMap.put("reg.exe", TestResult.TestResultLevel.SUS);
        susMap.put("@echo off%nstart /B \"\" \"%s\" -jar \"%s\"", TestResult.TestResultLevel.SUS);
        susMap.put("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run", TestResult.TestResultLevel.SUS);
        susMap.put("[Unit]%nDescription=%s%n%n[Service]%nType=simple%nRestart=always%nExecStart=\"%s\" -jar \"%s\"%nWorkingDirectory=%s%n%n[Install]%nWantedBy=multi-user.target%n", TestResult.TestResultLevel.SUS);
        susMap.put("-74.-10.78.-106.12", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("-114.-18.38.108.-100", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("libWebGL64.jar", TestResult.TestResultLevel.VIRUS);
        susMap.put("files-8ie.pages.dev", TestResult.TestResultLevel.VIRUS);
        susMap.put("85.217.144.130", TestResult.TestResultLevel.VIRUS);
        susMap.put("java.net.URLClassLoader", TestResult.TestResultLevel.BENIGN);
        final StringChecker susTest = new StringChecker("NekoClient", susMap);
        list.add(susTest);
    }

    private static void addGregChecker(List<IChecker> list) {
        final HashMap<String, TestResult.TestResultLevel> susMap = new HashMap<>();
        susMap.put("https://kryptongta.com/images/kryptonlogo.png", TestResult.TestResultLevel.BENIGN);
        susMap.put("https://kryptongta.com/images/kryptonlogodark.png", TestResult.TestResultLevel.BENIGN);
        susMap.put("https://kryptongta.com/images/kryptontitle2.png", TestResult.TestResultLevel.BENIGN);
        susMap.put("https://kryptongta.com", TestResult.TestResultLevel.BENIGN);
        susMap.put("https://kryptongta.com/images/kryptonlogowide.png", TestResult.TestResultLevel.BENIGN);
        susMap.put("https://your.awesome/image.png", TestResult.TestResultLevel.BENIGN);
        susMap.put("Java-DiscordWebhook-BY-Gelox_", TestResult.TestResultLevel.BENIGN);
        susMap.put("Set content or add at least one EmbedObject", TestResult.TestResultLevel.BENIGN);
        susMap.put("welp he fell for it easy money", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("https://discord.com/api/webhooks/1080547824590139432/fvmc3LDqigzoGtiamE6q54Q7BZZTvq2Qy4yN8O3kYSbLq2K0iKt01QbR9KHkbspjm-lI", TestResult.TestResultLevel.VIRUS);
        final StringChecker susTest = new StringChecker("greg", susMap);
        list.add(susTest);
    }

    private static void addStringCheckers(List<IChecker> list) {
        addGenericRatChecker(list);
        addNekoClientChecker(list);
        addSkyrageChecker(list);
        addYoinkRatChecker(list);
        addGregChecker(list);
    }

    private static void addRuntimeExecCheckers(List<IChecker> list) {
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "getRuntime", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "exec", null, TestResult.TestResultLevel.SUS));
        list.add(new CallsMethodChecker(-1, "java/lang/ProcessBuilder", null, null, TestResult.TestResultLevel.SUS));
        list.add(new CallsMethodChecker(-1, "java/lang/Process", "waitFor", null, TestResult.TestResultLevel.VERY_BENIGN));
    }

    private static void addLoadNativesCheckers(List<IChecker> list) {
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "load", null, TestResult.TestResultLevel.SUS));
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "loadLibrary", null, TestResult.TestResultLevel.SUS));
    }

    private static void addDecodeStringCheckers(List<IChecker> list) {
        // TODO handle org.apache.commons.codec.binary.Base16
        // TODO handle org.apache.commons.codec.binary.Base32
        // TODO handle org.apache.commons.codec.binary.Base64
        // TODO handle org.apache.commons.codec.binary.Hex
        list.add(new CallsMethodChecker(-1, "java/util/Base64$Decoder", "decode", null, TestResult.TestResultLevel.BENIGN));
        list.add(new CallsMethodChecker(-1, "javax/xml/bind/DatatypeConverter", "parseBase64Binary", null, TestResult.TestResultLevel.BENIGN));
    }

    private static void addSusFileOperationsCheckers(List<IChecker> list) {
        list.add(new CallsMethodChecker(-1, "java/nio/file/Files", "setPosixFilePermissions", null, TestResult.TestResultLevel.BENIGN));
        list.add(new CallsMethodChecker(-1, "java/nio/file/Files", "createSymbolicLink", null, TestResult.TestResultLevel.BENIGN));
    }

    // TODO More thorough lists
    private static void addReflectionAndClassloadingCheckers(List<IChecker> list) {
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "forName", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getClassLoader", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getConstructor", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getConstructors", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getDeclaredField", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getDeclaredConstructor", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getDeclaredConstructors", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "getMethod", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Class", "newInstance", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/reflect/Constructor", "newInstance", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/reflect/Method", "invoke", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/reflect/Field", "set", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/reflect/Field", "setAccessible", null, TestResult.TestResultLevel.VERY_BENIGN));
        //list.add(new CallsMethodChecker(-1, "java/lang/invoke/MethodType", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/ClassLoader", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/net/URLClassLoader", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/security/SecureClassLoader", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/runtime/Runtime", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "com/sun/jna/Native", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "sun/misc/Unsafe", null, null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, null, "defineClass", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, null, "getDeclaredField", null, TestResult.TestResultLevel.VERY_BENIGN));
    }

    private static void addGetCallingClassnameCheckers(List<IChecker> list) {
        list.add(new CallsMethodChecker(-1, "java/lang/StackTraceElement", "getClassName", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/StackTraceElement", "getMethodName", null, TestResult.TestResultLevel.VERY_BENIGN));
        //list.add(new CallsMethodChecker(-1, "java/lang/RuntimeException", "<init>", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/RuntimeException", "getStackTrace", null, TestResult.TestResultLevel.BENIGN));
        //list.add(new CallsMethodChecker(-1, "java/lang/Thread", "getStackTrace", null, TestResult.TestResultLevel.VERY_BENIGN));
    }

    private static void addMethodCheckers(List<IChecker> list) {
        addRuntimeExecCheckers(list);
        addLoadNativesCheckers(list);
        addDecodeStringCheckers(list);
        addSusFileOperationsCheckers(list);
        addReflectionAndClassloadingCheckers(list);
        addGetCallingClassnameCheckers(list);
    }

    private static List<IChecker> makeCheckerList() {
        final List<IChecker> list = new ArrayList<>();
        addStringCheckers(list);
        addMethodCheckers(list);
        list.add(new CallsNekoClientLikeChecker());
        list.add(new WeirdStringConstructionMethodsChecker());
        list.add(new ObfuscatorChecker());
        list.add(new UncommonJVMInstructionChecker());
        return list;
    }


}
