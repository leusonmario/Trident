/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server;

import net.tridentsdk.Server;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.config.Config;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.concurrent.TridentTick;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.util.JiraExceptionCatcher;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * This class represents the running Minecraft server
 */
@ThreadSafe
public class TridentServer implements Server {
    /**
     * The instance of the TridentServer, if it exists
     */
    private static volatile TridentServer instance;

    /**
     * The configuration file used by the server
     */
    private final ServerConfig config;
    /**
     * The logger to which the server logs
     */
    private final Logger logger;
    /**
     * The socket channel handler instance
     */
    private final NetServer server;
    /**
     * The ticking thread for the server
     */
    private final TridentTick tick;

    /**
     * Creates a new server instance
     *
     * @param config the config to initialize the server
     * @param console the logger to which the server logs
     */
    private TridentServer(ServerConfig config,
                          Logger console,
                          NetServer server) {
        this.config = config;
        this.logger = console;
        this.server = server;
        this.tick = new TridentTick(console);
    }

    /**
     * Init code for server startup
     */
    public static TridentServer init(ServerConfig config, Logger console,
                                     NetServer net) throws IllegalStateException {
        TridentServer server = new TridentServer(config, console, net);
        if (TridentServer.instance == null) {
            TridentServer.instance = server;
            return server;
        }

        throw new IllegalStateException("Server is already initialized");
    }

    /**
     * Obtains the singleton instance of the server
     * implementation.
     *
     * @return instance of server
     */
    public static TridentServer instance() {
        return TridentServer.instance;
    }

    /**
     * Shortcut method to retrieving the server config.
     *
     * @return the server config
     */
    public static ServerConfig cfg() {
        return (ServerConfig) instance().config();
    }

    @Override
    public String ip() {
        return this.config.ip();
    }

    @Override
    public int port() {
        return this.config.port();
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public Config config() {
        return this.config;
    }

    @Override
    public Collection<Player> players() {
        return Collections.unmodifiableCollection(TridentPlayer.PLAYERS.values());
    }

    @Override
    public void reload() {
        this.logger.warn("SERVER RELOADING...");

        try {
            this.logger.log("Saving config...");
            this.config.save();
        } catch (IOException e) {
            JiraExceptionCatcher.serverException(e);
            return;
        }

        this.logger.success("Server has reloaded successfully.");
    }

    @Override
    public void shutdown() {
        this.logger.warn("SERVER SHUTTING DOWN...");
        try {
            this.logger.log("Saving config...");
            this.config.save();
            this.tick.interrupt();
            this.logger.log("Closing network connections...");
            this.server.shutdown();
        } catch (IOException | InterruptedException e) {
            JiraExceptionCatcher.serverException(e);
            return;
        }

        this.logger.success("Server has shutdown successfully.");
    }
}