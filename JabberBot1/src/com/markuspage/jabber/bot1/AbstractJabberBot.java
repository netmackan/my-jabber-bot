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

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 *
 * @author markus
 */
public abstract class AbstractJabberBot implements JabberBot {
    private XMPPConnection connection;
    private final String serverHost;
    private final int serverPort;
    private final String serviceName;

    public AbstractJabberBot(String serverHost, int serverPort, String serviceName) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serviceName = serviceName;
    }

    @Override
    public void login(String userName, String password, String resource) throws XMPPException {
        ConnectionConfiguration config = new ConnectionConfiguration(serverHost, serverPort);
        config.setServiceName(serviceName);
        connection = new XMPPConnection(config);
        connection.connect();
        connection.login(userName, password, resource);
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    protected XMPPConnection getConnection() {
        return connection;
    }

}
