/*
 * Copyright (C) 2014 Markus Kilås
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.markuspage.jabber.bot1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 *
 * Sample config.properties:
 * <pre>
 * serverhost=im.example.com
 * serverport=5222
 * username=testuser1
 * password=foo123
 * servicename=example.com
 * </pre>
 *
 * @author Markus Kilås
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws org.jivesoftware.smack.XMPPException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws XMPPException, IOException {
        if (args.length < 1) {
            System.err.println("Usage: JabberBot1 <config.properties>");
            System.exit(-1);
            return;
        }

        // Load config file
        String configFile = args[0];
        Properties config = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(configFile)));
            config.load(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {} // NOPMD
            }
        }

        // Load config values
        String serverHost = config.getProperty("serverhost");
        int serverPort = Integer.parseInt(config.getProperty("serverport"));
        String username = config.getProperty("username");
        String password = config.getProperty("password");
        String serviceName = config.getProperty("servicename");

        System.out.println("serverHost: " + serverHost);
        System.out.println("serverPort: " + serverPort);
        System.out.println("username: " + username);
        System.out.println("password (length): " + password.length());
        System.out.println("serviceName: " + serviceName);

        // declare variables
        MyMessageListener c = new MyMessageListener(serverHost, serverPort, serviceName);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;

        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = true;

        // Enter your login information here
        c.login(username, password);

        c.displayBuddyList();

        System.out.println("-----");

        System.out.println("Who do you want to talk to? - Type contacts full email address:");
        String talkTo = br.readLine();

        System.out.println("-----");
        System.out.println("All messages will be sent to " + talkTo);
        System.out.println("Enter your message in the console:");
        System.out.println("-----\n");

        while (!(msg = br.readLine()).equals("bye")) {
            c.sendMessage(msg, talkTo);
        }

        c.disconnect();
        System.exit(0);
    }

}
