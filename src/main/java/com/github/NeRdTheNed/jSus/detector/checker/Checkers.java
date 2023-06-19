package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Checkers {

    public static final List<IChecker> checkerList = makeCheckerList();

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
            "/AppData/Roaming/discord/Local Storage/leveldb/",
            "/AppData/Roaming/discordptb/Local Storage/leveldb/",
            "/AppData/Roaming/discordcanary/Local Storage/leveldb/",
            "/AppData/Roaming/Opera Software/Opera Stable/Local Storage/leveldb/",
            "/AppData/Local/Google/Chrome/User Data/Default/Local Storage/leveldb/",
            "/AppData/Roaming/Mozilla/Firefox/Profiles/",
            "/AppData/Local/Microsoft/Edge/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/Vivaldi/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/Yandex/YandexBrowser/User Data/Default/Local Storage/leveldb/",
            "/AppData/Roaming/Yandex/YandexBrowser/User Data/Default/Local Storage/leveldb/",
            "/AppData/Local/BraveSoftware/Brave-Browser/User Data/Default/Local Storage/leveldb/",
            "/AppData/Roaming/BraveSoftware/Brave-Browser/User Data/Default/Local Storage/leveldb/",
            "/Library/Application Support/discord/Local Storage/leveldb/",
            "/Library/Application Support/discordptb/Local Storage/leveldb/",
            "/Library/Application Support/discordcanary/Local Storage/leveldb/",
            "/Library/Application Support/Firefox/Profiles/",
            "/Library/Application Support/Google/Chrome/User Data/Default/Local Storage/leveldb/",
            "/.metadata/.plugins/org.eclipse.buildship.ui/dialog_settings.xml",
            "<item key=\"project_location\" ",
            "/eclipse/configuration/.settings/org.eclipse.ui.ide.prefs",
            "wmic process get name,executablepath",
            "ps -e -o command",
            "428A487E3361EF9C5FC20233485EA236",
            "/AppData/Roaming/FileZilla/recentservers.xml",
            "rusherhack/alts.json",
            "rusherhack/waypoints.json",
            "/AppData/Roaming/JetBrains/",
            "/options/recentProjects.xml",
            "<option name=\"recentPaths\">",
            "https://pastebin.com/raw/X5UHFxtM",
            "89D85BE00F56ACE593BC029C686E9BA5",
            "99CE85B34778C8C765CD2F222748EF11",
            "F6DA144461738529DB35B7DC4E2578B2",
            "\nDiscord token[s]: \n",
            "\nMinecraft data: \n",
            "\nPyro loader credentials: \n",
            "\nFuture loader credentials:\n",
            "\nRusherHack loader credentials: \n",
            "\nPyro accounts:\n",
            "\nFuture accounts: \n",
            "\nRusherHack accounts:\n",
            "\nPyro waypoints:\n",
            "\nFuture waypoints: \n",
            "\nRusherHack waypoints:\n",
            "\nJourneyMap waypoints: \n",
            "\nKAMI Blue waypoints: \n",
            //"\nMinecraft mods:\n"
            "\nMultiMC accounts.json: \n",
            "\nEclipse workspaces:\n",
            "\nIntellij workspaces:\n",
            "\nFileZilla hosts:\n",
            //"\nDownloads folder:\n"
            //"\nDesktop folder:\n"
            //"\nuser.home:\n"
            "\nAdditional discord data:\n",
            //"\nRunning processes:\n"
            "http://dengimod.cf/discordhook/sendstuff2.php",
            "https://pastebin.com/raw/eiv5znvZ",
            // V1
            "https://pastebin.com/raw/jdiVNVZ2",
            "\\Google\\Chrome\\User Data\\Default\\Login Data",
            "\\Local Storage\\leveldb\\",
            "\\Future\\accounts.txt",
            "\u0024USER_HOME\u0024",
            "https://pastebin.com/raw/ZrMLRRar",
            "\\Google\\Chrome\\User Data\\Default",
            "\\Opera Software\\Opera Stable",
            "\\BraveSoftware\\Brave-Browser\\User Data\\Default",
            "\\Yandex\\YandexBrowser\\User Data\\Default",
            "\\LightCord",
            "\\Microsoft\\Edge\\User Data\\Default",
            "[\\w\\W]{24}\\.[\\w\\W]{6}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}",
            "\\Mozilla\\Firefox\\Profiles",
            // 1.5
            "/Google/Chrome/User Data/Local State", "/Google/Chrome/User Data/Default/Login Data",
            "/Library/Application Support/Google/Chrome/User Data/Local State", "/Library/Application Support/Google/Chrome/User Data/Default/Login Data",
            "/.config/google-chrome/Local State", "/.config/google-chrome/Default/Login Data",
            "/Opera Software/Opera Stable/Local State", "/Opera Software/Opera Stable/Default/Login Data",
            "/Library/Application Support/Opera/Opera/Local State", "/Library/Application Support/Opera/Opera/Default/Login Data",
            "/.config/opera/Local State", "/.config/opera/Default/Login Data",
            "/BraveSoftware/Brave-Browser/Local State", "/BraveSoftware/Brave-Browser/Default/Login Data",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Local State", "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Login Data",
            "/.config/BraveSoftware/Brave-Browser/Local State", "/.config/BraveSoftware/Brave-Browser/Default/Login Data",
            "/Yandex/YandexBrowser/User Data/Local State", "/Yandex/YandexBrowser/User Data/Default/Login Data",
            "/Library/Application Support/Yandex/YandexBrowser/Local State", "/Library/Application Support/Yandex/YandexBrowser/Default/Login Data",
            "/.config/Yandex/YandexBrowser/Local State", "/.config/Yandex/YandexBrowser/Default/Login Data",
            "/Microsoft/Edge/User Data/Local State", "/Microsoft/Edge/User Data/Default/Login Data",
            "/Library/Application Support/Microsoft/Edge/Local State", "/Library/Application Support/Microsoft/Edge/Default/Login Data",
            "/.config/Microsoft/Edge/Local State", "/.config/Microsoft/Edge/Default/Login Data",
            "SELECT `origin_url`,`username_value`,`password_value` from `logins`",
            // later
            "/Google/Chrome/User Data/Default/Web Data",
            "/Library/Application Support/Google/Chrome/User Data/Default/Web Data",
            "/.config/google-chrome/Default/Web Data",
            "/Opera Software/Opera Stable/Default/Web Data",
            "/Library/Application Support/Opera/Opera/Default/Web Data",
            "/.config/opera/Default/Web Data",
            "/BraveSoftware/Brave-Browser/Default/Web Data",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Web Data",
            "/.config/BraveSoftware/Brave-Browser/Default/Web Data",
            "/Yandex/YandexBrowser/User Data/Default/Web Data",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Web Data",
            "/.config/Yandex/YandexBrowser/Default/Web Data",
            "/Microsoft/Edge/User Data/Default/Web Data",
            "/Library/Application Support/Microsoft/Edge/Default/Web Data",
            "/.config/Microsoft/Edge/Default/Web Data",
            "SELECT date_created,date_last_used,name,value,count from `autofill` ORDER BY date_created",
            "NAME: %s\nVALUE: %s\nCREATED: %s\nLAST USED: %s\nCOUNT: %s\n------------\n",
            "/Google/Chrome/User Data/Default/Cookies",
            "/Library/Application Support/Google/Chrome/User Data/Default/Cookies",
            "/.config/google-chrome/Default/Cookies",
            "/Opera Software/Opera Stable/Default/Cookies",
            "/Library/Application Support/Opera/Opera/Default/Cookies",
            "/.config/opera/Default/Cookies",
            "/BraveSoftware/Brave-Browser/Default/Cookies",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Cookies",
            "/.config/BraveSoftware/Brave-Browser/Default/Cookies",
            "/Yandex/YandexBrowser/User Data/Default/Cookies",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Cookies",
            "/.config/Yandex/YandexBrowser/Default/Cookies",
            "/Microsoft/Edge/User Data/Default/Cookies",
            "/Library/Application Support/Microsoft/Edge/Default/Cookies",
            "/.config/Microsoft/Edge/Default/Cookies",
            "/Google/Chrome/User Data/Default/Network/Cookies",
            "/Library/Application Support/Google/Chrome/User Data/Default/Network/Cookies",
            "/.config/google-chrome/Default/Network/Cookies",
            "/Opera Software/Opera Stable/Default/Network/Cookies",
            "/Library/Application Support/Opera/Opera/Default/Network/Cookies",
            "/.config/opera/Default/Network/Cookies",
            "/BraveSoftware/Brave-Browser/Default/Network/Cookies",
            "/Library/Application Support/BraveSoftware/Brave-Browser/Default/Network/Cookies",
            "/.config/BraveSoftware/Brave-Browser/Default/Network/Cookies",
            "/Yandex/YandexBrowser/User Data/Default/Network/Cookies",
            "/Library/Application Support/Yandex/YandexBrowser/Default/Network/Cookies",
            "/.config/Yandex/YandexBrowser/Default/Network/Cookies",
            "/Microsoft/Edge/User Data/Default/Network/Cookies",
            "/Library/Application Support/Microsoft/Edge/Default/Network/Cookies",
            "/.config/Microsoft/Edge/Default/Network/Cookies",
            "SELECT `host_key`,`name`,`path`,`encrypted_value`,`expires_utc` from `cookies`",
            "HOST KEY: %s\nNAME: %s\nPATH: %s\nEXPIRES (UTC): %s\nVALUE: %s\n------------\n",
            "SELECT * from `credit_cards`",
            "NAME: %s\nDATE: %s/%s\nCARD: %s\n------------\n",
            "com.liberty.jaxx\\IndexedDB\\file__0.indexeddb.leveldb\\",
            "HKCU\\Software\\Bitcoin\\Bitcoin-Qt",
            "HKCU\\Software\\Dash\\Dash-Qt",
            "HKCU\\Software\\Litecoin\\Litecoin-Qt",
            "HKCU\\Software\\monero-project\\monero-core",
            //"/AppData/Roaming/Mozilla/Firefox/Profiles/",
            //"/AppData/Roaming/Waterfox/Profiles/",
            "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvdjYvdXNlcnMvQG1lL2JpbGxpbmcvcGF5bWVudC1zb3VyY2Vz",
            "\\recentservers.xml",
            "FileZilla/recentservers.xml",
            "/Google/Chrome/User Data",
            "FROM: %s\nURL: %s\nUSERNAME: %s\nPASSWORD: %s\n------------\n",
            "HKLM\\SOFTWARE\\WOW6432Node\\Valve\\Steam",
            "Steam/config/",
            "config\\loginusers.vdf",
            "Telegram Desktop\\tdata",
            "manatee.technology 1.5 | by juggenbande",
            "=========[SYSTEM INFO]=========\n",
            "=========[DISCORD INFO]=========\n",
            "=========[MINECRAFT]=========\n",
            "=========[PASSWORDS]=========\n",
            "=========[CRYPTO]=========\n",
            "=========[STEAM]=========\n",
            "=========[OTHER]=========\n",
            "Browser Cookies\n", "Browser Credit Cards\n", "Browser Autofill\n",
            "http://yoink.site/atlanta/%s.php",

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

            "/AppData/Local/Google/Chrome/User Data/",
            "C:\\Users\\",
            "/.config/discord/Cache/Local Storage/leveldb/",
            "/.config/discordcanary/Cache/Local Storage/leveldb/",
            "/.config/discordptb/Cache/Local Storage/leveldb/",

            "[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}",
            "mfa\\.[\\w-]{84}",
            "302094807046684672",
        };

        for (final String susString : strongSusStrings) {
            susMap.put(susString, TestResult.TestResultLevel.STRONG_SUS);
        }

        final String[] susStrings = {
            "https://steamcommunity.com/profiles/",
            "\"76(.*?)\"",
            "[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}",
            "[\\w\\.]{24}\\.[\\w\\.]{6}\\.[\\w\\.\\-]{27}|mfa\\.[\\w\\.\\-]{84}",
            "dQw4w9WgXcQ:[^\"]*",
            "/Future/waypoints.txt",
            "/Future/accounts.txt",
            "Pyro/launcher.json",
            "KAMIBlueWaypoints.json",
            "/Future/auth_key",
            "Pyro/alts.json",
            "Pyro/server/",
            "https://discordapp.com/api/v6/users/@me/billing/payment-sources",
            "\\Future\\backup",
            "Pyro\\alts.json",
            "\\.minecraft\\Pyro\\server",
            "rusherhack\\alts.json",
            "rusherhack\\waypoints.json",
            "\\.minecraft\\SalHack\\Waypoints\\Waypoints.json",
            "\\Documents\\ShareX\\",
            "@everyone NEW LOG ",
            "https://cdn.discordapp.com/attachments/761105850194329600/765200019488899102/5ccabf62108d5a8074ddd95af2211727.png",
            "https://cdn.discordapp.com/avatars/703469635416096839/a_fdaa18602fc0a9b5ce3577a54d2ca262.webp",
            "Armory\\", "Crypto/Armory",
            "atomic\\Local Storage\\leveldb\\", "Crypto/AtomicWallet",
            "Electrum\\wallets\\", "Crypto/Electrum",
            "Ethereum\\keystore\\", "Crypto/Ethereum",
            "Exodus\\exodus.wallet\\", "Crypto/Exodus",
            "Zcash\\", "Crypto/Zcash",
            "Crypto/Jaxx",
            "bytecoin\\", ".wallet", "Crypto/Bytecoin",
            "strDataDir", "\\wallet.dat", "Crypto/BitcoinCore",
            "Crypto/DashCore",
            "Crypto/LitecoinCore",
            "wallet_path", "Crypto/MoneroCore",
            "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvdjYvdXNlcnMvQG1l",
            "cHJlbWl1bV90eXBl",
            "https://discordapp.com/api/v7/invites/minecraft"
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
            "t23e7v6uz8idz87ehugwq.skyrage.de"
        };

        for (final String virusString : virusStrings) {
            susMap.put(virusString, TestResult.TestResultLevel.VIRUS);
        }

        susMap.put("/plugi", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("n-config.bin", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("plugin-config.bin", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("/plugin-config.bin", TestResult.TestResultLevel.STRONG_SUS);
        susMap.put("REPLACE HEREEEE", TestResult.TestResultLevel.SUS);
        susMap.put("LWphc", TestResult.TestResultLevel.SUS);
        susMap.put("-Dgnu=", TestResult.TestResultLevel.SUS);
        susMap.put("/bin/java", TestResult.TestResultLevel.SUS);
        susMap.put("\\bin\\javaw.exe", TestResult.TestResultLevel.SUS);
        final StringChecker susTest = new StringChecker("Skyrage", susMap);
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
        addNekoClientChecker(list);
        addSkyrageChecker(list);
        addYoinkRatChecker(list);
        addGregChecker(list);
    }

    private static List<IChecker> makeCheckerList() {
        final List<IChecker> list = new ArrayList<>();
        addStringCheckers(list);
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "getRuntime", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "load", null, TestResult.TestResultLevel.SUS));
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "loadLibrary", null, TestResult.TestResultLevel.SUS));
        list.add(new CallsMethodChecker(-1, "java/lang/Runtime", "exec", null, TestResult.TestResultLevel.SUS));
        list.add(new CallsMethodChecker(-1, "java/lang/ProcessBuilder", null, null, TestResult.TestResultLevel.SUS));
        // TODO handle org.apache.commons.codec.binary.Base16
        // TODO handle org.apache.commons.codec.binary.Base32
        // TODO handle org.apache.commons.codec.binary.Base64
        // TODO handle org.apache.commons.codec.binary.Hex
        list.add(new CallsMethodChecker(-1, "java/util/Base64$Decoder", "decode", null, TestResult.TestResultLevel.BENIGN));
        list.add(new CallsMethodChecker(-1, "javax/xml/bind/DatatypeConverter", "parseBase64Binary", null, TestResult.TestResultLevel.BENIGN));
        list.add(new CallsMethodChecker(-1, "java/lang/Process", "waitFor", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsMethodChecker(-1, "java/nio/file/Files", "setPosixFilePermissions", null, TestResult.TestResultLevel.BENIGN));
        list.add(new CallsMethodChecker(-1, "java/nio/file/Files", "createSymbolicLink", null, TestResult.TestResultLevel.BENIGN));
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
        //list.add(new CallsMethodChecker(-1, "java/lang/StackTraceElement", "getMethodName", null, TestResult.TestResultLevel.VERY_BENIGN));
        list.add(new CallsNekoClientLikeChecker());
        list.add(new WeirdStringConstructionMethodsChecker());
        list.add(new ObfuscatorChecker());
        list.add(new UncommonJVMInstructionChecker());
        return list;
    }


}
