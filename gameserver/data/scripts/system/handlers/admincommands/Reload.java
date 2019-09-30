/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ne.commons.annotations.NotNull;
import com.ne.commons.func.tuple.Tuple2;
import com.ne.commons.utils.Callback;
import com.ne.commons.utils.Chainer;
import com.ne.gs.configs.Config;
import com.ne.gs.configs.administration.ChatCommandsConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.dataholders.EventData;
import com.ne.gs.dataholders.QuestsData;
import com.ne.gs.dataholders.SkillData;
import com.ne.gs.dataholders.StaticData;
import com.ne.gs.dataholders.XMLQuests;
import com.ne.gs.dataholders.loadingutils.XmlDataLoader;
import com.ne.gs.dataholders.loadingutils.XmlValidationHandler;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.handlers.CmdReloadHandler;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.modules.common.CustomLocManager;
import com.ne.gs.modules.customrates.CustomRateManager;
import com.ne.gs.modules.customrifts.CustomRiftManager;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.services.EventService;
import com.ne.gs.services.reward.RewardService;
import com.ne.gs.skillengine.model.SkillTemplate;
import com.ne.gs.utils.chathandlers.ChatCommand;

import static org.apache.commons.io.filefilter.FileFilterUtils.*;

public class Reload extends ChatCommand {

    private static final String USAGE = "syntax //reload <quest | skill | portal | spawn | cmd | drop | gameshop | " +
            "events | config | customrift | customloc | customrate>";
    private static final Logger log = LoggerFactory.getLogger(Reload.class);

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws
            Exception {
        if (params.length != 1) {
            admin.sendMsg(USAGE);
            return;
        }
        if (params[0].equals("quest")) {
            File xml = new File("./data/static_data/quest_data/quest_data.xml");
            File dir = new File("./data/static_data/quest_script_data");
            try {
                QuestEngine.getInstance().shutdown();
                JAXBContext jc = JAXBContext.newInstance(StaticData.class);
                Unmarshaller un = jc.createUnmarshaller();
                un.setSchema(getSchema("./data/static_data/static_data.xsd"));
                QuestsData newQuestData = (QuestsData) un.unmarshal(xml);
                QuestsData questsData = DataManager.QUEST_DATA;
                questsData.setQuestsData(newQuestData.getQuestsData());
                XMLQuests questScriptsData = DataManager.XML_QUESTS;
                questScriptsData.getQuest().clear();
                for (File file : listFiles(dir, true)) {
                    XMLQuests data = (XMLQuests) un.unmarshal(file);
                    if (data != null) {
                        if (data.getQuest() != null) {
                            questScriptsData.getQuest().addAll(data.getQuest());
                        }
                    }
                }
                QuestEngine.getInstance().load(null);
            } catch (Exception e) {
                admin.sendMsg("Quest reload failed!");
                log.error("quest reload fail", e);
            } finally {
                admin.sendMsg("Quest reload Success!");
            }
        } else if (params[0].equals("skill")) {
            File dir = new File("./data/static_data/skills");
            try {
                JAXBContext jc = JAXBContext.newInstance(StaticData.class);
                Unmarshaller un = jc.createUnmarshaller();
                un.setSchema(getSchema("./data/static_data/static_data.xsd"));
                List<SkillTemplate> newTemplates = new ArrayList<>();
                for (File file : listFiles(dir, true)) {
                    Object obj = un.unmarshal(file);
                    if (obj instanceof SkillData) {
                        SkillData data = (SkillData) un.unmarshal(file);
                        if (data != null) {
                            newTemplates.addAll(data.getSkillTemplates());
                        }
                    }
                }
                DataManager.SKILL_DATA.setSkillTemplates(newTemplates);
            } catch (Exception e) {
                admin.sendMsg("Skill reload failed!");
                log.error("Skill reload failed!", e);
            } finally {
                admin.sendMsg("Skill reload Success!");
            }
        } else if (params[0].equals("zone")) {
            try {
                admin.sendMsg("Reloading zones");
                // dirty quick hack :)
                XmlDataLoader loader = XmlDataLoader.getInstance();
                StaticData data = loader.loadStaticData();
                DataManager.ZONE_DATA = data.zoneData;
            } catch (Exception e) {
                admin.sendMsg("Zones reload failed!");
                log.error("Zones reload failed!", e);
            } finally {
                admin.sendMsg("Zones reloaded!");
            }
        } else if (params[0].equals("portal")) {
            // File dir = new File("./data/static_data/portals");
            try {
                JAXBContext jc = JAXBContext.newInstance(StaticData.class);
                Unmarshaller un = jc.createUnmarshaller();
                un.setSchema(getSchema("./data/static_data/static_data.xsd"));
                // List<PortalTemplate> newTemplates = new ArrayList<PortalTemplate>();
                // for (File file : listFiles(dir, true)) {
                // PortalData data = (PortalData) un.unmarshal(file);
                // if (data != null && data.getPortals() != null)
                // newTemplates.addAll(data.getPortals());
                // }
                // DataManager.PORTAL_DATA.setPortals(newTemplates);
            } catch (Exception e) {
                admin.sendMsg("Portal reload failed!");
                log.error("Portal reload failed!", e);
            } finally {
                admin.sendMsg("Portal reload Success!");
            }
        } else if (params[0].equals("config")) {
            Config.reload();
            admin.sendMsg("Configs successfully reloaded!");
        }
        // Needs to be implented in NpcDropData.java
        /**
         * else if (params[0].equals("drop")) {
         * NpcDropData npcDropData = NpcDropData.load();
         * DataManager.NPC_DROP_DATA = npcDropData;
         * PacketSendUtility.sendMessage(admin, "NpcDrops successfully reloaded!");
         * }
         */
        else if (params[0].equals("gameshop")) {
            InGameShopEn.getInstance().reload();
            admin.sendMsg("Gameshop successfully reloaded!");
        } else if (params[0].equals("events")) {
            File eventXml = new File("./data/static_data/events_config/events_config.xml");
            EventData data;
            try {
                JAXBContext jc = JAXBContext.newInstance(EventData.class);
                Unmarshaller un = jc.createUnmarshaller();
                un.setEventHandler(new XmlValidationHandler());
                un.setSchema(getSchema("./data/static_data/static_data.xsd"));
                data = (EventData) un.unmarshal(eventXml);
            } catch (Exception e) {
                admin.sendMsg("Event reload failed! Keeping the last version ...");
                log.error("Event reload failed!", e);
                return;
            }
            if (data != null) {
                EventService.getInstance().stop();
                String text = data.getActiveText();
                if (text == null || text.trim().length() == 0) {
                    text = "NONE";
                }
                DataManager.EVENT_DATA.setAllEvents(data.getAllEvents(), data.getActiveText());
                admin.sendMsg("Active events: " + text);
                EventService.getInstance().start();
            }
        } else if (params[0].equals("cmd")) {
            ChatCommandsConfig.reload();
            admin.sendMsg("Commands reloaded");
        } else if (params[0].equals("customrift")) { // TODO move to separate handler later
            CustomRiftManager.REF.tell(new CustomRiftManager.Init(new MessageCallback(admin)));
        } else if (params[0].equals("customloc")) { // TODO move to separate handler later
            CustomLocManager.getInstance().tell(new CustomLocManager.Init(new MessageCallback(admin)));
        } else if (params[0].equals("customrate")) { // TODO move to separate handler later
            CustomRateManager.getInstance().tell(new CustomRateManager.Init(new MessageCallback(admin)));
        } else if (params[0].equals("custommedal")) { // TODO move to separate handler later
            RewardService.getInstance().reloadConfig();
        } else {
            if (!Chainer.GLOBAL.handle(CmdReloadHandler.class, Tuple2.of(admin, params)))
                admin.sendMsg(USAGE);
        }
    }

    private Schema getSchema(String xml_schema) {
        Schema schema;
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            schema = sf.newSchema(new File(xml_schema));
        } catch (SAXException saxe) {
            throw new Error("Error while getting schema", saxe);
        }

        return schema;
    }

    private Collection<File> listFiles(File root, boolean recursive) {
        IOFileFilter dirFilter = recursive ? makeSVNAware(HiddenFileFilter.VISIBLE) : null;

        return FileUtils.listFiles(root, and(and(notFileFilter(prefixFileFilter("new")), suffixFileFilter(".xml")),
                HiddenFileFilter.VISIBLE), dirFilter);
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg(USAGE);
    }


    public static class MessageCallback implements Callback<String, Object> {
        private final Player _p;

        MessageCallback(Player p) {
            _p = p;
        }

        @Override
        public Object onEvent(@NotNull String msg) {
            _p.sendMsg(msg);
            return null;
        }
    }
}
