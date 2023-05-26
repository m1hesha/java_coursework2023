import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NetworkMonitoringApp {

    private static final String CONFIG_FILE = "config.properties";

    public static void main(String[] args) {
        Properties config = loadConfiguration();

        int monitoringInterval = Integer.parseInt(config.getProperty("monitoringInterval"));
        boolean trackNetworkLoad = Boolean.parseBoolean(config.getProperty("trackNetworkLoad"));
        boolean trackCPUUsage = Boolean.parseBoolean(config.getProperty("trackCPUUsage"));
        boolean trackMemoryUsage = Boolean.parseBoolean(config.getProperty("trackMemoryUsage"));
        boolean trackDiskUsage = Boolean.parseBoolean(config.getProperty("trackDiskUsage"));
        boolean trackDNSDelay = Boolean.parseBoolean(config.getProperty("trackDNSDelay"));

        startMonitoring(monitoringInterval, trackNetworkLoad, trackCPUUsage, trackMemoryUsage, trackDiskUsage, trackDNSDelay);
    }

    private static Properties loadConfiguration() {
        Properties config = new Properties();
        try {
            InputStream inputStream = NetworkMonitoringApp.class.getResourceAsStream("/resources/config.properties");
            config.load(inputStream);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке конфигурационного файла: " + e.getMessage());
        }
        return config;
    }



    private static void startMonitoring(int interval, boolean trackNetworkLoad, boolean trackCPUUsage,
                                        boolean trackMemoryUsage, boolean trackDiskUsage, boolean trackDNSDelay) {
        while (true) {
            if (trackNetworkLoad) {
                trackNetworkLoad();
            }

            if (trackCPUUsage) {
                trackCPUUsage();
            }

            if (trackMemoryUsage) {
                trackMemoryUsage();
            }

            if (trackDiskUsage) {
                trackDiskUsage();
            }

            if (trackDNSDelay) {
                trackDNSDelay();
            }

            try {
                Thread.sleep(interval * 10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void trackNetworkLoad() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ip", "addr");
            Process process = processBuilder.start();
            String output = readProcessOutput(process);
            System.out.println("Отслеживание нагрузки на сетевые интерфейсы:\n" + output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void trackCPUUsage() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "top -b -1 -n 1 -w 200");
            Process process = processBuilder.start();
            String output = readProcessOutput(process);
            System.out.println("Отслеживание загрузки потоков процессора:\n" + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void trackMemoryUsage() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("free");
            Process process = processBuilder.start();
            String output = readProcessOutput(process);
            System.out.println("Отслеживание заполнения памяти:\n" + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void trackDiskUsage() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh","-c", "df");
            Process process = processBuilder.start();
            String output = readProcessOutput(process);
            System.out.println("Отслеживание заполнения диска:\n" + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void trackDNSDelay() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "5", "8.8.8.8");
            Process process = processBuilder.start();
            String output = readProcessOutput(process);
            System.out.println("Отслеживание задержки запросов до DNS серверов:\n" + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        java.io.InputStream inputStream = process.getInputStream();
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        java.io.InputStream errorStream = process.getErrorStream();
        java.io.BufferedReader errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(errorStream));
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            System.err.println(errorLine);
        }
        return output.toString();
    }
}