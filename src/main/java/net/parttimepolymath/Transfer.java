package net.parttimepolymath;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.parttimepolymath.controller.Controller;
import net.parttimepolymath.controller.ControllerHolder;
import net.parttimepolymath.controller.ControllerImpl;
import net.parttimepolymath.model.DataStore;
import net.parttimepolymath.model.DataStoreFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * main executable class. Note that there are no unit tests against this class, as it's just the runtime framework.
 */
public class Transfer {
    /**
     * application properties loaded from the classpath.
     */
    private static final Properties PROPERTIES = loadProperties();

    /**
     * main entry point.
     * 
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("?", "help", false, "print this message");
        options.addOption("v", "version", false, "print version");
        options.addOption("p", "port", true, "specify the port to run on (defaults to 8080)");
        options.addOption("x", "test", false, "executes in test mode against a running instance");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption('?')) {
                doHelp(options);
            } else if (cmd.hasOption('v')) {
                doVersion();
            } else {
                int port;
                if (cmd.hasOption('p')) {
                    port = NumberUtils.toInt(StringUtils.strip(cmd.getOptionValue('p')), 8080);
                } else {
                    port = 8080;
                }

                if (cmd.hasOption('x')) {
                    executeTest(port);
                } else {
                    executeServer(port);
                }
            }
        } catch (ParseException ex) {
            doHelp(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * run the tests against a running server.
     */
    private static void executeTest(final int port) {
        Tester test = new Tester(port);
        test.execute();
    }

    /**
     * run the server instance. Note that this is a blocking call - we disappear into the jetty server until
     * the JVM is halted.
     * 
     * @throws IOException if we cannot read resources
     */
    private static void executeServer(final int port) throws IOException {
        String createScript = IOUtils.toString(Transfer.class.getResourceAsStream("/createDB.sql"), "UTF-8");
        DataStore dataStore = DataStoreFactory.makeDataStore(createScript);
        Controller controller = new ControllerImpl(dataStore);
        ControllerHolder.setController(controller);

        JettyServer instance = new JettyServer(port);
        try {
            ControllerHolder.getController().activate();
            instance.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * report the application version to standard out.
     */
    private static void doVersion() {
        String name = PROPERTIES.getProperty("project.name");
        String version = PROPERTIES.getProperty("project.version");
        System.out.println(String.format("%s [%s]", name, version));
    }

    /**
     * print command line options to standard out.
     * 
     * @param options the command line options.
     */
    private static void doHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(PROPERTIES.getProperty("project.name"), options);
    }

    /**
     * load properties from the class path.
     * 
     * @return a Properties object, which should be non-null unless theres an exception.
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("project.properties");
            props.load(in);
        } catch (IOException ioe) {
            System.err.println("Failed to read application properties");
        }
        return props;
    }
}
