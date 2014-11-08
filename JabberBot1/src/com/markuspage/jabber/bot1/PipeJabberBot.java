/*
 * Copyright (C) 2014 markus
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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Bot echoing the content of each file in a directory.
 *
 * @author Markus Kilås
 */
public class PipeJabberBot extends AbstractJabberBot {

    private final LinkedList<MultiUserChat> mucs = new LinkedList<MultiUserChat>();

    private final MessageListener messageListener = new MessageListener() {

        @Override
        public void processMessage(Chat chat, Message msg) {
            if (msg.getType() == Message.Type.chat && msg.getBody() != null) {
                try {
                    chat.sendMessage("Hi, I don't know what you mean. I am just a bot. See you in a chat room.");
                } catch (XMPPException ex) {
                    Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    };

    private final PacketListener packetListener = new PacketListener() {

        private int messageCount = 0;

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof Message) {
                Message msg = (Message) packet;
                System.out.println("Received message number : " + (messageCount++) + ": " + msg.getBody());
            }
        }

        public int getMessageCount() {
            return messageCount;
        }
    };

    public PipeJabberBot(String serverHost, int serverPort, String serviceName) {
        super(serverHost, serverPort, serviceName);
    }

    @Override
    public void login(String userName, String password, String resource) throws XMPPException {
        super.login(userName, password, resource);
        getConnection().getChatManager().addChatListener(new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(messageListener);
                System.out.println("Chat started with: " + chat.getParticipant());
            }
        });
    }

    public void joinMUC(final String room, final String nickname) {
        try {
            MultiUserChat muc = new MultiUserChat(getConnection(), room);
            muc.addMessageListener(packetListener);
            muc.join(nickname);
            mucs.add(muc);
            System.out.println("joined conversation!");
        } catch (XMPPException ex) {
            Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Process each file in the folder.
     * @param folder to read from
     * @throws IOException in case of error
     */
    public void processFolder(final File folder) throws IOException {
        if (!folder.exists()) {
            throw new IOException("No such folder: " + folder.getAbsolutePath());
        }
        for (File file : folder.listFiles()) {
            System.out.println("Found file: " + file.getName());
            processFile(file);
        }
    }

    /**
     * Start monitoring the folder for new files.
     * @param folder to read from
     * @throws IOException in case of error
     */
    public void monitorFolder(final File folder) throws IOException {
        if (!folder.exists()) {
            throw new IOException("No such folder: " + folder.getAbsolutePath());
        }

        FileAlterationObserver observer = new FileAlterationObserver(folder);
        FileAlterationMonitor monitor = new FileAlterationMonitor(5 * 1000);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate(File file) {
                System.out.println("File created: " + file.getName());
                processFile(file);
            }

        };

        observer.addListener(listener);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception ex) {
            Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex);
            for (MultiUserChat muc : mucs) {
                try {
                    muc.sendMessage("I failed to start monitoring: " + ex.getMessage());
                } catch (XMPPException ex1) {
                    Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    private void processFile(final File file) {
        try {
            String message = org.apache.commons.io.FileUtils.readFileToString(file);
            for (MultiUserChat muc : mucs) {
                try {
                    muc.sendMessage(message);
                } catch (XMPPException ex) {
                    Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            file.delete();
        } catch (IOException ex) {
            Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex);
            for (MultiUserChat muc : mucs) {
                try {
                    muc.sendMessage("I failed to read file: " + ex.getMessage());
                } catch (XMPPException ex1) {
                    Logger.getLogger(PipeJabberBot.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

}
