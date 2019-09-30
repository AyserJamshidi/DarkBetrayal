/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.annotations.NotNull;
import com.ne.commons.database.DB;
import com.ne.commons.database.DatabaseFactory;
import com.ne.commons.database.IUStH;
import com.ne.commons.database.ParamReadStH;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.ChatUtil;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.WorldMapType;

/**
 * @author Mrakobes
 * @modified antness
 */
public class Bk extends ChatCommand {

    ArrayList<Bookmark> bookmarks = new ArrayList<>();
    private static final Logger log = (Logger) LoggerFactory.getLogger(Bk.class);
    private String bookmark_name = "";

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("syntax //bk <add|del|tele|list>");
            return;
        }

        if (params[0].equals("add")) {
            try {
                bookmark_name = params[1].toLowerCase();
                if (isBookmarkExists(bookmark_name, player.getObjectId())) {
                    player.sendMsg("Bookmark " + bookmark_name + " already exists !");
                    return;
                }

                final float x = player.getX();
                final float y = player.getY();
                final float z = player.getZ();
                final int char_id = player.getObjectId();
                final int world_id = player.getWorldId();

                DB.insertUpdate("INSERT INTO bookmark (`name`,`char_id`, `x`, `y`, `z`,`world_id` ) VALUES (?, ?, ?, ?, ?, ?)", new IUStH() {
                    @Override
                    public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
                        ps.setString(1, bookmark_name);
                        ps.setInt(2, char_id);
                        ps.setFloat(3, x);
                        ps.setFloat(4, y);
                        ps.setFloat(5, z);
                        ps.setInt(6, world_id);
                        ps.execute();
                    }
                });

                player.sendMsg("Bookmark " + bookmark_name + " sucessfully added to your bookmark list!");

                updateInfo(player.getObjectId());
            } catch (Exception e) {
                player.sendMsg("syntax //bk <add|del|tele> <bookmark name>");
            }
        } else if (params[0].equals("del")) {
            Connection con = null;
            try {
                bookmark_name = params[1].toLowerCase();
                con = DatabaseFactory.getConnection();

                PreparedStatement statement = con.prepareStatement("DELETE FROM bookmark WHERE name = ?");
                statement.setString(1, bookmark_name);
                statement.executeUpdate();
                statement.close();
            } catch (Exception e) {
                player.sendMsg("syntax //bk <add|del|tele> <bookmark name>");
            } finally {
                DatabaseFactory.close(con);
                player.sendMsg("Bookmark " + bookmark_name + " sucessfully removed from your bookmark list!");
                updateInfo(player.getObjectId());
            }
        } else if (params[0].equals("tele")) {
            try {

                if (params[1].equals("") || params[1] == null) {
                    player.sendMsg("syntax //bk <add|del|tele> <bookmark name>");
                    return;
                }

                updateInfo(player.getObjectId());

                bookmark_name = params[1].toLowerCase();
                Bookmark tele_bk = null;
                try {
                    tele_bk = selectByName(bookmark_name);
                } finally {
                    if (tele_bk != null) {
                        TeleportService.teleportTo(player, tele_bk.getWorld_id(), tele_bk.getX(), tele_bk.getY(), tele_bk.getZ(), (byte) 0,
                            TeleportAnimation.BEAM_ANIMATION);
                        player.sendMsg("Teleported to bookmark " + tele_bk.getName() + " location");
                    }
                }
            } catch (Exception e) {
                player.sendMsg("syntax //bk <add|del|tele> <bookmark name>");
            }
        } else if (params[0].equals("list")) {
            updateInfo(player.getObjectId());
            player.sendMsg("=====Bookmark list begin=====");
            for (Bookmark b : bookmarks) {
                String chatLink = ChatUtil.position(b.getName(), b.getWorld_id(), b.getX(), b.getY(), b.getZ());
                player.sendMsg(" = " + chatLink + " =  " + WorldMapType.of(b.getWorld_id()) + "  ( " + b.getX() + " ," + b.getY()
                    + " ," + b.getZ() + " )");
            }
            player.sendMsg("=====Bookmark list end=======");
        }
    }

    /**
     * Reload bookmark list from db
     */
    public void updateInfo(final int objId) {
        bookmarks.clear();

        DB.select("SELECT * FROM `bookmark` where char_id= ?", new ParamReadStH() {

            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, objId);
            }

            @Override
            public void handleRead(ResultSet rset) throws SQLException {
                while (rset.next()) {
                    String name = rset.getString("name");
                    float x = rset.getFloat("x");
                    float y = rset.getFloat("y");
                    float z = rset.getFloat("z");
                    int world_id = rset.getInt("world_id");
                    bookmarks.add(new Bookmark(x, y, z, world_id, name));
                }
            }
        });
    }

    /**
     * @param bk_name
     *     - bookmark name
     *
     * @return Bookmark from bookmark name
     */
    public Bookmark selectByName(String bk_name) {
        for (Bookmark b : bookmarks) {
            if (b.getName().equals(bk_name)) {
                return b;
            }
        }
        return null;
    }

    /**
     * @param bk_name
     *     - bookmark name
     *
     * @return true if bookmark exists
     */
    public boolean isBookmarkExists(String bk_name, int objId) {
        Connection con = null;
        int bkcount = 0;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT count(id) as bkcount FROM bookmark WHERE ? = name AND char_id = ?");
            statement.setString(1, bk_name);
            statement.setInt(2, objId);
            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                bkcount = rset.getInt("bkcount");
            }
            rset.close();
            statement.close();
        } catch (Exception e) {
            log.error("Error in reading db", e);
        } finally {
            DatabaseFactory.close(con);
        }
        return bkcount > 0;
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //bk <add|del|tele|list>");
    }

    class Bookmark {

        private final String name;
        private final float x;
        private final float y;
        private final float z;
        private final int world_id;

        public Bookmark(float x, float y, float z, int world_id, String name) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world_id = world_id;
            this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the x
         */
        public float getX() {
            return x;
        }

        /**
         * @return the y
         */
        public float getY() {
            return y;
        }

        /**
         * @return the z
         */
        public float getZ() {
            return z;
        }

        /**
         * @return the world_id
         */
        public int getWorld_id() {
            return world_id;
        }
    }

}


