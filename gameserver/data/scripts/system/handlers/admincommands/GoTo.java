/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.instance.InstanceService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;
import com.ne.gs.world.WorldMap;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldMapType;

/**
 * Goto command
 *
 * @author Dwarfpicker
 * @rework Imaginary
 */
public class GoTo extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("syntax //goto <location>");
            return;
        }

        StringBuilder sbDestination = new StringBuilder();
        for (String p : params) {
            sbDestination.append(p).append(" ");
        }

        String destination = sbDestination.toString().trim();

        /**
         * Elysea
         */
        // Sanctum
        if (destination.equalsIgnoreCase("Sanctum")) {
            goTo(player, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
        } else if (destination.equalsIgnoreCase("Kaisinel")) {
            goTo(player, WorldMapType.KAISINEL.getId(), 2155, 1567, 1205);
        } else if (destination.equalsIgnoreCase("Poeta")) {
            goTo(player, WorldMapType.POETA.getId(), 806, 1242, 119);
        } else if (destination.equalsIgnoreCase("Melponeh")) {
            goTo(player, WorldMapType.POETA.getId(), 426, 1740, 119);
        } else if (destination.equalsIgnoreCase("Verteron")) {
            goTo(player, WorldMapType.VERTERON.getId(), 1643, 1500, 119);
        } else if (destination.equalsIgnoreCase("Cantas") || destination.equalsIgnoreCase("Cantas Coast")) {
            goTo(player, WorldMapType.VERTERON.getId(), 2384, 788, 102);
        } else if (destination.equalsIgnoreCase("Ardus") || destination.equalsIgnoreCase("Ardus Shrine")) {
            goTo(player, WorldMapType.VERTERON.getId(), 2333, 1817, 193);
        } else if (destination.equalsIgnoreCase("Pilgrims") || destination.equalsIgnoreCase("Pilgrims Respite")) {
            goTo(player, WorldMapType.VERTERON.getId(), 2063, 2412, 274);
        } else if (destination.equalsIgnoreCase("Tolbas") || destination.equalsIgnoreCase("Tolbas Village")) {
            goTo(player, WorldMapType.VERTERON.getId(), 1291, 2206, 142);
        } else if (destination.equalsIgnoreCase("Eltnen")) {
            goTo(player, WorldMapType.ELTNEN.getId(), 343, 2724, 264);
        } else if (destination.equalsIgnoreCase("Golden") || destination.equalsIgnoreCase("Golden Bough Garrison")) {
            goTo(player, WorldMapType.ELTNEN.getId(), 688, 431, 332);
        } else if (destination.equalsIgnoreCase("Eltnen Observatory")) {
            goTo(player, WorldMapType.ELTNEN.getId(), 1779, 883, 422);
        } else if (destination.equalsIgnoreCase("Novan")) {
            goTo(player, WorldMapType.ELTNEN.getId(), 947, 2215, 252);
        } else if (destination.equalsIgnoreCase("Agairon")) {
            goTo(player, WorldMapType.ELTNEN.getId(), 1921, 2045, 361);
        } else if (destination.equalsIgnoreCase("Kuriullu")) {
            goTo(player, WorldMapType.ELTNEN.getId(), 2411, 2724, 361);
        } else if (destination.equalsIgnoreCase("Theobomos")) {
            goTo(player, WorldMapType.THEOBOMOS.getId(), 1398, 1557, 31);
        } else if (destination.equalsIgnoreCase("Jamanok") || destination.equalsIgnoreCase("Jamanok Inn")) {
            goTo(player, WorldMapType.THEOBOMOS.getId(), 458, 1257, 127);
        } else if (destination.equalsIgnoreCase("Meniherk")) {
            goTo(player, WorldMapType.THEOBOMOS.getId(), 1396, 1560, 31);
        } else if (destination.equalsIgnoreCase("obsvillage")) {
            goTo(player, WorldMapType.THEOBOMOS.getId(), 2234, 2284, 50);
        } else if (destination.equalsIgnoreCase("Josnack")) {
            goTo(player, WorldMapType.THEOBOMOS.getId(), 901, 2774, 62);
        } else if (destination.equalsIgnoreCase("Anangke")) {
            goTo(player, WorldMapType.THEOBOMOS.getId(), 2681, 847, 138);
        } else if (destination.equalsIgnoreCase("Heiron")) {
            goTo(player, WorldMapType.HEIRON.getId(), 2540, 343, 411);
        } else if (destination.equalsIgnoreCase("Heiron Observatory")) {
            goTo(player, WorldMapType.HEIRON.getId(), 1423, 1334, 175);
        } else if (destination.equalsIgnoreCase("Senemonea")) {
            goTo(player, WorldMapType.HEIRON.getId(), 971, 686, 135);
        } else if (destination.equalsIgnoreCase("Jeiaparan")) {
            goTo(player, WorldMapType.HEIRON.getId(), 1635, 2693, 115);
        } else if (destination.equalsIgnoreCase("Changarnerk")) {
            goTo(player, WorldMapType.HEIRON.getId(), 916, 2256, 157);
        } else if (destination.equalsIgnoreCase("Kishar")) {
            goTo(player, WorldMapType.HEIRON.getId(), 1999, 1391, 118);
        } else if (destination.equalsIgnoreCase("Arbolu")) {
            goTo(player, WorldMapType.HEIRON.getId(), 170, 1662, 120);
        } else if (destination.equalsIgnoreCase("Pandaemonium") || destination.equalsIgnoreCase("panda")) {
            goTo(player, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
        } else if (destination.equalsIgnoreCase("Marchutan")) {
            goTo(player, WorldMapType.MARCHUTAN.getId(), 1557, 1429, 266);
        } else if (destination.equalsIgnoreCase("Ishalgen")) {
            goTo(player, WorldMapType.ISHALGEN.getId(), 529, 2449, 281);
        } else if (destination.equalsIgnoreCase("Anturoon")) {
            goTo(player, WorldMapType.ISHALGEN.getId(), 940, 1707, 259);
        } else if (destination.equalsIgnoreCase("Altgard")) {
            goTo(player, WorldMapType.ALTGARD.getId(), 1748, 1807, 254);
        } else if (destination.equalsIgnoreCase("Basfelt")) {
            goTo(player, WorldMapType.ALTGARD.getId(), 1903, 696, 260);
        } else if (destination.equalsIgnoreCase("Trader")) {
            goTo(player, WorldMapType.ALTGARD.getId(), 2680, 1024, 311);
        } else if (destination.equalsIgnoreCase("Impetusiom")) {
            goTo(player, WorldMapType.ALTGARD.getId(), 2643, 1658, 324);
        } else if (destination.equalsIgnoreCase("Altgard Observatory")) {
            goTo(player, WorldMapType.ALTGARD.getId(), 1468, 2560, 299);
        } else if (destination.equalsIgnoreCase("Morheim")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 308, 2274, 449);
        } else if (destination.equalsIgnoreCase("Desert")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 634, 900, 360);
        } else if (destination.equalsIgnoreCase("Slag")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 1772, 1662, 197);
        } else if (destination.equalsIgnoreCase("Kellan")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 1070, 2486, 239);
        } else if (destination.equalsIgnoreCase("Alsig")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 2387, 1742, 102);
        } else if (destination.equalsIgnoreCase("Morheim Observatory")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 2794, 1122, 171);
        } else if (destination.equalsIgnoreCase("Halabana")) {
            goTo(player, WorldMapType.MORHEIM.getId(), 2346, 2219, 127);
        } else if (destination.equalsIgnoreCase("Brusthonin")) {
            goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15);
        } else if (destination.equalsIgnoreCase("Baltasar")) {
            goTo(player, WorldMapType.BRUSTHONIN.getId(), 1413, 2013, 51);
        } else if (destination.equalsIgnoreCase("Bollu")) {
            goTo(player, WorldMapType.BRUSTHONIN.getId(), 840, 2016, 307);
        } else if (destination.equalsIgnoreCase("Edge")) {
            goTo(player, WorldMapType.BRUSTHONIN.getId(), 1523, 374, 231);
        } else if (destination.equalsIgnoreCase("Bubu")) {
            goTo(player, WorldMapType.BRUSTHONIN.getId(), 526, 848, 76);
        } else if (destination.equalsIgnoreCase("Settlers")) {
            goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2417, 15);
        } else if (destination.equalsIgnoreCase("Beluslan")) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
        } else if (destination.equalsIgnoreCase("Besfer")) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 533, 1866, 262);
        } else if (destination.equalsIgnoreCase("Kidorun")) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 1243, 819, 260);
        } else if (destination.equalsIgnoreCase("Red Mane")) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 2358, 1241, 470);
        } else if (destination.equalsIgnoreCase("Kistenian")) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 1942, 513, 412);
        } else if (destination.equalsIgnoreCase("Hoarfrost")) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 2431, 2063, 579);
        } else if (destination.equalsIgnoreCase("Inggison")) {
            goTo(player, WorldMapType.INGGISON.getId(), 1335, 276, 590);
        } else if (destination.equalsIgnoreCase("Ufob")) {
            goTo(player, WorldMapType.INGGISON.getId(), 382, 951, 460);
        } else if (destination.equalsIgnoreCase("Soteria")) {
            goTo(player, WorldMapType.INGGISON.getId(), 2713, 1477, 382);
        } else if (destination.equalsIgnoreCase("Hanarkand")) {
            goTo(player, WorldMapType.INGGISON.getId(), 1892, 1748, 327);
        } else if (destination.equalsIgnoreCase("Gelkmaros")) {
            goTo(player, WorldMapType.GELKMAROS.getId(), 1763, 2911, 554);
        } else if (destination.equalsIgnoreCase("Subterranea")) {
            goTo(player, WorldMapType.GELKMAROS.getId(), 2503, 2147, 464);
        } else if (destination.equalsIgnoreCase("Rhonnam")) {
            goTo(player, WorldMapType.GELKMAROS.getId(), 845, 1737, 354);
        } else if (destination.equalsIgnoreCase("Silentera")) {
            goTo(player, 600010000, 583, 767, 300);
        } else if (destination.equalsIgnoreCase("Reshanta")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 951, 936, 1667);
        } else if (destination.equalsIgnoreCase("Abyss 1")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 2867, 1034, 1528);
        } else if (destination.equalsIgnoreCase("Abyss 2")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 1078, 2839, 1636);
        } else if (destination.equalsIgnoreCase("Abyss 3")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 1596, 2952, 2943);
        } else if (destination.equalsIgnoreCase("Abyss 4")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 2054, 660, 2843);
        } else if (destination.equalsIgnoreCase("Eye of Reshanta") || destination.equalsIgnoreCase("Eye")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 1979, 2114, 2291);
        } else if (destination.equalsIgnoreCase("Divine Fortress") || destination.equalsIgnoreCase("Divine")) {
            goTo(player, WorldMapType.RESHANTA.getId(), 2130, 1925, 2322);
        } else if (destination.equalsIgnoreCase("Haramel")) {
            goTo(player, 300200000, 176, 21, 144);
        } else if (destination.equalsIgnoreCase("Nochsana") || destination.equalsIgnoreCase("NTC")) {
            goTo(player, 300030000, 513, 668, 331);
        } else if (destination.equalsIgnoreCase("Arcanis") || destination.equalsIgnoreCase("Sky Temple of Arcanis")) {
            goTo(player, 320050000, 177, 229, 536);
        } else if (destination.equalsIgnoreCase("Fire Temple") || destination.equalsIgnoreCase("FT")) {
            goTo(player, 320100000, 144, 312, 123);
        } else if (destination.equalsIgnoreCase("Kromede") || destination.equalsIgnoreCase("Kromede Trial")) {
            goTo(player, 300230000, 248, 244, 189);
        } else if (destination.equalsIgnoreCase("Steel Rake") || destination.equalsIgnoreCase("SR")) {
            goTo(player, 300100000, 237, 506, 948);
        } else if (destination.equalsIgnoreCase("Steel Rake Lower") || destination.equalsIgnoreCase("SR Low")) {
            goTo(player, 300100000, 283, 453, 903);
        } else if (destination.equalsIgnoreCase("Steel Rake Middle") || destination.equalsIgnoreCase("SR Mid")) {
            goTo(player, 300100000, 283, 453, 953);
        } else if (destination.equalsIgnoreCase("Indratu") || destination.equalsIgnoreCase("Indratu Fortress")) {
            goTo(player, 310090000, 562, 335, 1015);
        } else if (destination.equalsIgnoreCase("Azoturan") || destination.equalsIgnoreCase("Azoturan Fortress")) {
            goTo(player, 310100000, 458, 428, 1039);
        } else if (destination.equalsIgnoreCase("Bio Lab") || destination.equalsIgnoreCase("Aetherogenetics Lab")) {
            goTo(player, 310050000, 225, 244, 133);
        } else if (destination.equalsIgnoreCase("Adma") || destination.equalsIgnoreCase("Adma Stronghold")) {
            goTo(player, 320130000, 450, 200, 168);
        } else if (destination.equalsIgnoreCase("Alquimia") || destination.equalsIgnoreCase("Alquimia Research Center")) {
            goTo(player, 320110000, 603, 527, 200);
        } else if (destination.equalsIgnoreCase("Draupnir") || destination.equalsIgnoreCase("Draupnir Cave")) {
            goTo(player, 320080000, 491, 373, 622);
        } else if (destination.equalsIgnoreCase("Theobomos Lab") || destination.equalsIgnoreCase("Theobomos Research Lab")) {
            goTo(player, 310110000, 477, 201, 170);
        } else if (destination.equalsIgnoreCase("Dark Poeta") || destination.equalsIgnoreCase("DP")) {
            goTo(player, 300040000, 1214, 412, 140);
        } else if (destination.equalsIgnoreCase("Sulfur") || destination.equalsIgnoreCase("Sulfur Tree Nest")) {
            goTo(player, 300060000, 462, 345, 163);
        } else if (destination.equalsIgnoreCase("Right Wing") || destination.equalsIgnoreCase("Right Wing Chamber")) {
            goTo(player, 300090000, 263, 386, 103);
        } else if (destination.equalsIgnoreCase("Left Wing") || destination.equalsIgnoreCase("Left Wing Chamber")) {
            goTo(player, 300080000, 672, 606, 321);
        } else if (destination.equalsIgnoreCase("Asteria Chamber")) {
            goTo(player, 300050000, 469, 568, 202);
        } else if (destination.equalsIgnoreCase("Miren Chamber")) {
            goTo(player, 300130000, 527, 120, 176);
        } else if (destination.equalsIgnoreCase("Kysis Chamber")) {
            goTo(player, 300120000, 528, 121, 176);
        } else if (destination.equalsIgnoreCase("Krotan Chamber")) {
            goTo(player, 300140000, 528, 109, 176);
        } else if (destination.equalsIgnoreCase("Roah Chamber")) {
            goTo(player, 300070000, 504, 396, 94);
        } else if (destination.equalsIgnoreCase("Abyssal Splinter") || destination.equalsIgnoreCase("Core")) {
            goTo(player, 300220000, 704, 153, 453);
        } else if (destination.equalsIgnoreCase("Dredgion")) {
            goTo(player, 300110000, 414, 193, 431);
        } else if (destination.equalsIgnoreCase("Chantra") || destination.equalsIgnoreCase("Chantra Dredgion")) {
            goTo(player, 300210000, 414, 193, 431);
        } else if (destination.equalsIgnoreCase("Taloc") || destination.equalsIgnoreCase("Taloc's Hollow")) {
            goTo(player, 300190000, 200, 214, 1099);
        } else if (destination.equalsIgnoreCase("Udas") || destination.equalsIgnoreCase("Udas Temple")) {
            goTo(player, 300150000, 637, 657, 134);
        } else if (destination.equalsIgnoreCase("Udas Lower") || destination.equalsIgnoreCase("Udas Lower Temple")) {
            goTo(player, 300160000, 1146, 277, 116);
        } else if (destination.equalsIgnoreCase("Beshmundir") || destination.equalsIgnoreCase("BT") || destination.equalsIgnoreCase("Beshmundir Temple")) {
            goTo(player, 300170000, 1477, 237, 243);
        } else if (destination.equalsIgnoreCase("Padmaraska Cave")) {
            goTo(player, 320150000, 385, 506, 66);
        } else if (destination.equalsIgnoreCase("Karamatis 0")) {
            goTo(player, 310010000, 221, 250, 206);
        } else if (destination.equalsIgnoreCase("Karamatis 1")) {
            goTo(player, 310020000, 312, 274, 206);
        } else if (destination.equalsIgnoreCase("Karamatis 2")) {
            goTo(player, 310120000, 221, 250, 206);
        } else if (destination.equalsIgnoreCase("Aerdina")) {
            goTo(player, 310030000, 275, 168, 205);
        } else if (destination.equalsIgnoreCase("Gerania")) {
            goTo(player, 310040000, 275, 168, 205);
        } else if (destination.equalsIgnoreCase("Sliver") || destination.equalsIgnoreCase("Sliver of Darkness")) {
            goTo(player, 310070000, 247, 249, 1392);
        } else if (destination.equalsIgnoreCase("Space") || destination.equalsIgnoreCase("Space of Destiny")) {
            goTo(player, 320070000, 246, 246, 125);
        } else if (destination.equalsIgnoreCase("Ataxiar 1")) {
            goTo(player, 320010000, 221, 250, 206);
        } else if (destination.equalsIgnoreCase("Ataxiar 2")) {
            goTo(player, 320020000, 221, 250, 206);
        } else if (destination.equalsIgnoreCase("Bregirun")) {
            goTo(player, 320030000, 275, 168, 205);
        } else if (destination.equalsIgnoreCase("Nidalber")) {
            goTo(player, 320040000, 275, 168, 205);
        } else if (destination.equalsIgnoreCase("Sanctum Arena")) {
            goTo(player, 310080000, 275, 242, 159);
        } else if (destination.equalsIgnoreCase("Triniel Arena")) {
            goTo(player, 320090000, 275, 239, 159);
        } else if (destination.equalsIgnoreCase("Crucible 1-0")) {
            goTo(player, 300300000, 380, 350, 95);
        } else if (destination.equalsIgnoreCase("Crucible 1-1")) {
            goTo(player, 300300000, 346, 350, 96);
        } else if (destination.equalsIgnoreCase("Crucible 5-0")) {
            goTo(player, 300300000, 1265, 821, 359);
        } else if (destination.equalsIgnoreCase("Crucible 5-1")) {
            goTo(player, 300300000, 1256, 797, 359);
        } else if (destination.equalsIgnoreCase("Crucible 6-0")) {
            goTo(player, 300300000, 1596, 150, 129);
        } else if (destination.equalsIgnoreCase("Crucible 6-1")) {
            goTo(player, 300300000, 1628, 155, 126);
        } else if (destination.equalsIgnoreCase("Crucible 7-0")) {
            goTo(player, 300300000, 1813, 797, 470);
        } else if (destination.equalsIgnoreCase("Crucible 7-1")) {
            goTo(player, 300300000, 1785, 797, 470);
        } else if (destination.equalsIgnoreCase("Crucible 8-0")) {
            goTo(player, 300300000, 1776, 1728, 304);
        } else if (destination.equalsIgnoreCase("Crucible 8-1")) {
            goTo(player, 300300000, 1776, 1760, 304);
        } else if (destination.equalsIgnoreCase("Crucible 9-0")) {
            goTo(player, 300300000, 1357, 1748, 320);
        } else if (destination.equalsIgnoreCase("Crucible 9-1")) {
            goTo(player, 300300000, 1334, 1741, 316);
        } else if (destination.equalsIgnoreCase("Crucible 10-0")) {
            goTo(player, 300300000, 1750, 1255, 395);
        } else if (destination.equalsIgnoreCase("Crucible 10-1")) {
            goTo(player, 300300000, 1761, 1280, 395);
        } else if (destination.equalsIgnoreCase("Arena Of Chaos - 1")) {
            goTo(player, 300350000, 1332, 1078, 340);
        } else if (destination.equalsIgnoreCase("Arena Of Chaos - 2")) {
            goTo(player, 300350000, 599, 1854, 227);
        } else if (destination.equalsIgnoreCase("Arena Of Chaos - 3")) {
            goTo(player, 300350000, 663, 265, 512);
        } else if (destination.equalsIgnoreCase("Arena Of Chaos - 4")) {
            goTo(player, 300350000, 1840, 1730, 302);
        } else if (destination.equalsIgnoreCase("Arena Of Chaos - 5")) {
            goTo(player, 300350000, 1932, 1228, 270);
        } else if (destination.equalsIgnoreCase("Arena Of Chaos - 6")) {
            goTo(player, 300350000, 1949, 946, 224);
        } else if (destination.equalsIgnoreCase("Prison LF") || destination.equalsIgnoreCase("Prison Elyos")) {
            goTo(player, 510010000, 256, 256, 49);
        } else if (destination.equalsIgnoreCase("Prison DF") || destination.equalsIgnoreCase("Prison Asmos")) {
            goTo(player, 520010000, 256, 256, 49);
        } else if (destination.equalsIgnoreCase("Test Dungeon")) {
            goTo(player, 300020000, 104, 66, 25);
        } else if (destination.equalsIgnoreCase("Test Basic")) {
            goTo(player, 900020000, 144, 136, 20);
        } else if (destination.equalsIgnoreCase("Test Server")) {
            goTo(player, 900030000, 228, 171, 49);
        } else if (destination.equalsIgnoreCase("Test GiantMonster")) {
            goTo(player, 900100000, 196, 187, 20);
        } else if (destination.equalsIgnoreCase("IDAbPro")) {
            goTo(player, 300010000, 270, 200, 206);
        } else if (destination.equalsIgnoreCase("gm")) {
            goTo(player, 120020000, 1442, 1133, 302);
        } else if (destination.equalsIgnoreCase("Kaisinel Academy")) {
            goTo(player, 110070000, 459, 251, 128);
        } else if (destination.equalsIgnoreCase("Marchutan Priory")) {
            goTo(player, 120080000, 577, 250, 94);
        } else if (destination.equalsIgnoreCase("Esoterrace")) {
            goTo(player, 300250000, 333, 437, 326);
        } else if (destination.equalsIgnoreCase("1012")) {
            goTo(player, 400010000, 2163, 2194, 2395);
        } else if (destination.equalsIgnoreCase("1013")) {
            goTo(player, 400010000, 2370, 2044, 2278);
        } else if (destination.equalsIgnoreCase("1014")) {
            goTo(player, 400010000, 2292, 2159, 2281);
        } else if (destination.equalsIgnoreCase("1015")) {
            goTo(player, 400010000, 1873, 1833, 2268);
        } else if (destination.equalsIgnoreCase("1016")) {
            goTo(player, 400010000, 2241, 1645, 2263);
        } else if (destination.equalsIgnoreCase("1017")) {
            goTo(player, 400010000, 2030, 1639, 2244);
        } else if (destination.equalsIgnoreCase("1018")) {
            goTo(player, 400010000, 2429, 1895, 2268);
        } else if (destination.equalsIgnoreCase("1019")) {
            goTo(player, 400010000, 1951, 2103, 2262);
        } else if (destination.equalsIgnoreCase("1020")) {
            goTo(player, 400010000, 2035, 2187, 2278);
        } else if (destination.equalsIgnoreCase("1142")) {
            goTo(player, 400010000, 1313, 1272, 1514);
        } else if (destination.equalsIgnoreCase("1143")) {
            goTo(player, 400010000, 1259, 1073, 1695);
        } else if (destination.equalsIgnoreCase("1144")) {
            goTo(player, 400010000, 1508, 1008, 1696);
        } else if (destination.equalsIgnoreCase("1145")) {
            goTo(player, 400010000, 1539, 1292, 1694);
        } else if (destination.equalsIgnoreCase("1146")) {
            goTo(player, 400010000, 1316, 1382, 1694);
        } else if (destination.equalsIgnoreCase("1133")) {
            goTo(player, 400010000, 2716, 2397, 1554);
        } else if (destination.equalsIgnoreCase("1134")) {
            goTo(player, 400010000, 2450, 2722, 1566);
        } else if (destination.equalsIgnoreCase("1135")) {
            goTo(player, 400010000, 2909, 2841, 1453);
        } else if (destination.equalsIgnoreCase("1212")) {
            goTo(player, 400010000, 2838, 714, 2889);
        } else if (destination.equalsIgnoreCase("1213")) {
            goTo(player, 400010000, 2570, 851, 2896);
        } else if (destination.equalsIgnoreCase("1214")) {
            goTo(player, 400010000, 3008, 904, 2845);
        } else if (destination.equalsIgnoreCase("1215")) {
            goTo(player, 400010000, 2939, 562, 2889);
        } else if (destination.equalsIgnoreCase("1222")) {
            goTo(player, 400010000, 2177, 1170, 3015);
        } else if (destination.equalsIgnoreCase("1223")) {
            goTo(player, 400010000, 2058, 1450, 2966);
        } else if (destination.equalsIgnoreCase("1224")) {
            goTo(player, 400010000, 2145, 1940, 3104);
        } else if (destination.equalsIgnoreCase("1232")) {
            goTo(player, 400010000, 2601, 2010, 3082);
        } else if (destination.equalsIgnoreCase("1233")) {
            goTo(player, 400010000, 2417, 2221, 3083);
        } else if (destination.equalsIgnoreCase("1242")) {
            goTo(player, 400010000, 1728, 2146, 2948);
        } else if (destination.equalsIgnoreCase("1243")) {
            goTo(player, 400010000, 1723, 2447, 2940);
        } else if (destination.equalsIgnoreCase("1252")) {
            goTo(player, 400010000, 701, 3186, 2912);
        } else if (destination.equalsIgnoreCase("1253")) {
            goTo(player, 400010000, 840, 3026, 3048);
        } else if (destination.equalsIgnoreCase("1254")) {
            goTo(player, 400010000, 635, 2881, 2687);
        } else if (destination.equalsIgnoreCase("2012")) {
            goTo(player, 210050000, 1888, 1859, 301);
        } else if (destination.equalsIgnoreCase("2013")) {
            goTo(player, 210050000, 1551, 2105, 322);
        } else if (destination.equalsIgnoreCase("2022")) {
            goTo(player, 210050000, 838, 1480, 360);
        } else if (destination.equalsIgnoreCase("2023")) {
            goTo(player, 210050000, 695, 1665, 329);
        } else if (destination.equalsIgnoreCase("3012")) {
            goTo(player, 220070000, 1264, 1176, 266);
        } else if (destination.equalsIgnoreCase("3013")) {
            goTo(player, 220070000, 1119, 1511, 283);
        } else if (destination.equalsIgnoreCase("3022")) {
            goTo(player, 220070000, 2047, 1468, 333);
        } else if (destination.equalsIgnoreCase("3023")) {
            goTo(player, 220070000, 1671, 1301, 327);
        } else if (destination.equalsIgnoreCase("1011")) {
            goTo(player, 400010000, 2143, 1900, 2275);
        } else if (destination.equalsIgnoreCase("1131")) {
            goTo(player, 400010000, 2791, 2608, 1503);
        } else if (destination.equalsIgnoreCase("1132")) {
            goTo(player, 400010000, 2621, 2857, 1461);
        } else if (destination.equalsIgnoreCase("1141")) {
            goTo(player, 400010000, 1393, 1183, 1468);
        } else if (destination.equalsIgnoreCase("1211")) {
            goTo(player, 400010000, 2677, 805, 2827);
        } else if (destination.equalsIgnoreCase("1221")) {
            goTo(player, 400010000, 2070, 1283, 2918);
        } else if (destination.equalsIgnoreCase("1231")) {
            goTo(player, 400010000, 2495, 2120, 3004);
        } else if (destination.equalsIgnoreCase("1241")) {
            goTo(player, 400010000, 1775, 2267, 2904);
        } else if (destination.equalsIgnoreCase("1251")) {
            goTo(player, 400010000, 694, 2982, 2766);
        } else if (destination.equalsIgnoreCase("2011")) {
            goTo(player, 210050000, 1725, 2251, 328);
        } else if (destination.equalsIgnoreCase("2021")) {
            goTo(player, 210050000, 892, 1992, 341);
        } else if (destination.equalsIgnoreCase("3021")) {
            goTo(player, 220070000, 1878, 1027, 330);
        } else if (destination.equalsIgnoreCase("3011")) {
            goTo(player, 220070000, 1194, 788, 314);
        } else if (destination.equalsIgnoreCase("4011")) {
            goTo(player, 600030000, 299.99603f, 354.61847f, 318.8442f);
        } else if (destination.equalsIgnoreCase("4021")) {
            goTo(player, 600030000, 2806.2935f, 271.178f, 304.35422f);
        } else if (destination.equalsIgnoreCase("4031")) {
            goTo(player, 600030000, 537.9461f, 2869.3105f, 324.5703f);
        } else if (destination.equalsIgnoreCase("4041")) {
            goTo(player, 600030000, 2765.473f, 2669.5264f, 359.6842f);
        } else if (destination.equalsIgnoreCase("4012")) {
            goTo(player, 600030000, 1258.8403f, 1219.9974f, 249.39062f);
        } else if (destination.equalsIgnoreCase("4013")) {
            goTo(player, 600030000, 1386.4255f, 1034.6503f, 231.89247f);
        } else if (destination.equalsIgnoreCase("4022")) {
            goTo(player, 600030000, 1772.5448f, 1220.5474f, 251.42184f);
        } else if (destination.equalsIgnoreCase("4023")) {
            goTo(player, 600030000, 1994.6837f, 1312.4482f, 233.24228f);
        } else if (destination.equalsIgnoreCase("4032")) {
            goTo(player, 600030000, 1667.8345f, 2020.9148f, 235.42322f);
        } else if (destination.equalsIgnoreCase("4033")) {
            goTo(player, 600030000, 1419.807f, 2015.0775f, 235.26225f);
        } else if (destination.equalsIgnoreCase("4042")) {
            goTo(player, 600030000, 1967.6696f, 1750.998f, 233.24185f);
        } else if (destination.equalsIgnoreCase("4043")) {
            goTo(player, 600030000, 1790.3389f, 1853.4569f, 248.3693f);
        } else if (destination.equalsIgnoreCase("4051")) {
            goTo(player, 600030000, 1484.9023f, 1534.3589f, 15.444983f);
        } else if (destination.equalsIgnoreCase("4052")) {
            goTo(player, 600030000, 1487.5634f, 1430.7158f, 145.13547f);
        } else if (destination.equalsIgnoreCase("4053")) {
            goTo(player, 600030000, 1482.652f, 1631.8999f, 145.12778f);
        } else if (destination.equalsIgnoreCase("Sarpan")) {
            goTo(player, 600020000, 1368, 1463, 600);
        } else if (destination.equalsIgnoreCase("Tiamaranta")) {
            goTo(player, 600030000, 13, 1731, 297);
        } else if (destination.equalsIgnoreCase("Oriel")) {
            goTo(player, 700010000, 1214, 1910, 95);
        } else if (destination.equalsIgnoreCase("Pernon")) {
            goTo(player, 710010000, 1067, 1452, 95);
        } else if (destination.equalsIgnoreCase("Aturam")) {
            goTo(player, 300240000, 558, 536, 601);
        } else if (destination.equalsIgnoreCase("Elementis")) {
            goTo(player, 300260000, 170, 616, 232);
        } else if (destination.equalsIgnoreCase("Argent")) {
            goTo(player, 300270000, 995, 1206, 66);
        } else if (destination.equalsIgnoreCase("Rentus")) {
            goTo(player, 300280000, 586, 125, 47);
        } else if (destination.equalsIgnoreCase("Raksang")) {
            goTo(player, 300310000, 857, 949, 1207);
        } else if (destination.equalsIgnoreCase("Muadas")) {
            goTo(player, 300380000, 457, 634, 126);
        } else if (destination.equalsIgnoreCase("TiamatDown")) {
            goTo(player, 600040000, 754, 99, 1197);
        } else {
            player.sendMsg("Could not find the specified destination !");
        }
    }

    private static void goTo(Player player, int worldId, float x, float y, float z) {
        WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
        if (destinationMap.isInstanceType()) {
            TeleportService.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
        } else {
            TeleportService.teleportTo(player, worldId, x, y, z, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
        }
    }

    private static int getInstanceId(int worldId, Player player) {
        if (player.getWorldId() == worldId) {
            WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
            if (registeredInstance != null) {
                return registeredInstance.getInstanceId();
            }
        }
        WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
        InstanceService.registerPlayerWithInstance(newInstance, player);
        return newInstance.getInstanceId();
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax : //goto <location>");
    }
}
