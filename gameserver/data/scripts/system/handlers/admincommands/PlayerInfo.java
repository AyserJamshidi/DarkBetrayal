/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import javolution.util.FastList;

import com.google.common.base.Predicate;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.account.PlayerAccountData;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Letter;
import com.ne.gs.model.gameobjects.Pet;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Mailbox;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.model.skill.PlayerSkillEntry;
import com.ne.gs.model.team.legion.Legion;
import com.ne.gs.model.team.legion.LegionMemberEx;
import com.ne.gs.model.team2.group.PlayerGroup;
import com.ne.gs.services.LegionService;
import com.ne.gs.utils.ChatUtil;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author lyahim @modified antness
 */
public class PlayerInfo extends ChatCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects
	 * .player.Player, java.lang.String, java.lang.String[])
	 */
	@Override
	protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
		if (params.length < 1) {
			admin.sendMsg("syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars | knownlist[info|add|remove] | visual[see|notsee]> ");
			return;
		}

		Player target = World.getInstance().findPlayer(params[0]);

		if (target == null) {
			admin.sendMsg("Selected player is not online!");
			return;
		}

		admin.sendMsg("\n[Info about " + target.getName() + "]\n-common: lv" + target.getLevel() + "(" + target.getCommonData().getExpShown() + " xp), " + target.getRace() + ", " + target.getPlayerClass() + "\n-ip: " + target.getClientConnection().getIP() + "\n" + "-account name: " + target.getClientConnection().getAccount().getName() + "\n");

		if (params.length < 2) {
			return;
		}

		if (params[1].equals("item")) {
			StringBuilder strbld = new StringBuilder("-items in inventory:\n");

			List<Item> items = target.getInventory().getItemsWithKinah();
			Iterator<Item> it = items.iterator();

			if (items.isEmpty()) {
				strbld.append("none\n");
			} else {
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    ").append(act.getItemCount()).append("(s) of ").append(ChatUtil.item(act.getItemTemplate().getTemplateId())).append("\n");
				}
			}

            items = target.getEquipment().getEquippedItems();
			it = items.iterator();
			strbld.append("-equipped items:\n");
			if (items.isEmpty()) {
				strbld.append("none\n");
			} else {
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    ").append(act.getItemCount()).append("(s) of ").append(ChatUtil.item(act.getItemTemplate().getTemplateId())).append("\n");
				}
			}

			items = target.getWarehouse().getItemsWithKinah();
			it = items.iterator();
			strbld.append("-items in warehouse:\n");
			if (items.isEmpty()) {
				strbld.append("none\n");
			} else {
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    ").append(act.getItemCount()).append("(s) of ").append("[item:").append(act.getItemTemplate().getTemplateId()).append("]").append("\n");
				}
			}
			showAllLines(admin, strbld.toString());
		} else if (params[1].equals("group")) {
			final StringBuilder strbld = new StringBuilder("-group info:\n  Leader: ");

			PlayerGroup group = target.getPlayerGroup2();
			if (group == null) {
				admin.sendMsg("-group info: no group");
			} else {
				strbld.append(group.getLeader().getName()).append("\n  Members:\n");
				group.applyOnMembers(new Predicate<Player>() {

					@Override
					public boolean apply(Player player) {
						strbld.append("    ").append(player.getName()).append("\n");
						return true;
					}

				});
				admin.sendMsg(strbld.toString());
			}

		} else if (params[1].equals("skill")) {
			StringBuilder strbld = new StringBuilder("-list of skills:\n");

			for (PlayerSkillEntry aSle : target.getSkillList().getAllSkills()) {
				strbld.append("    level ").append(aSle.getSkillLevel()).append(" of ").append(aSle.getSkillName()).append("\n");
			}
			showAllLines(admin, strbld.toString());
		} else if (params[1].equals("loc")) {
			String chatLink = ChatUtil.position(target.getName(), target.getPosition());
			admin.sendMsg("- " + chatLink + "'s location:\n  mapid: " + target.getWorldId() + "\n  X: " + target.getX() + " Y: " + target.getY() + "Z: " + target.getZ() + "heading: " + target.getHeading());
		} else if (params[1].equals("legion")) {
			StringBuilder strbld = new StringBuilder();

			Legion legion = target.getLegion();
			if (legion == null) {
				admin.sendMsg("-legion info: no legion");
			} else {
				ArrayList<LegionMemberEx> legionmemblist = LegionService.getInstance().loadLegionMemberExList(legion, null);
				Iterator<LegionMemberEx> it = legionmemblist.iterator();

				strbld.append("-legion info:\n  name: ").append(legion.getLegionName()).append(", level: ").append(legion.getLegionLevel()).append("\n  members(online):\n");
				while (it.hasNext()) {
					LegionMemberEx act = it.next();
					strbld.append("    ").append(act.getName()).append("(").append(act.isOnline() ? "online" : "offline").append(")").append(act.getRank().toString()).append("\n");
				}
			}
			showAllLines(admin, strbld.toString());
		} else if (params[1].equals("ap")) {
			admin.sendMsg("AP info about " + target.getName());
			admin.sendMsg("Total AP = " + target.getAbyssRank().getAp());
			admin.sendMsg("Total Kills = " + target.getAbyssRank().getAllKill());
			admin.sendMsg("Today Kills = " + target.getAbyssRank().getDailyKill());
			admin.sendMsg("Today AP = " + target.getAbyssRank().getDailyAP());
		} else if (params[1].equals("chars")) {
			admin.sendMsg("Others characters of " + target.getName() + " (" + target.getClientConnection().getAccount().size() + ") :");

			for (PlayerAccountData d : target.getClientConnection().getAccount()) {
				if (d != null && d.getPlayerCommonData() != null) {
					admin.sendMsg(d.getPlayerCommonData().getName());
				}
			}
		} else if (params[1].equals("knownlist")) {
			if (params[2].equals("info")) {
				admin.sendMsg("KnownList of " + target.getName());

				for (VisibleObject obj : target.getKnownList().getKnownObjects().values()) {
					admin.sendMsg(obj.getName() + " objectId:" + obj.getObjectId());
				}
			} else if (params[2].equals("add")) {
				int objId = Integer.parseInt(params[3]);
				VisibleObject obj = World.getInstance().findVisibleObject(objId);
				if (obj != null && !target.getKnownList().getKnownObjects().containsKey(objId)) {
					target.getKnownList().getKnownObjects().put(objId, obj);
				}
			} else if (params[2].equals("remove")) {
				int objId = Integer.parseInt(params[3]);
				VisibleObject obj = World.getInstance().findVisibleObject(objId);
				if (obj != null && target.getKnownList().getKnownObjects().containsKey(objId)) {
					target.getKnownList().getKnownObjects().remove(objId);
				}
			}
		} else if (params[1].equals("visual")) {
			if (params[2].equals("see")) {
				int objId = Integer.parseInt(params[3]);
				Player player = World.getInstance().findPlayer(objId);
				target.getController().see(player);
			} else if (params[2].equals("notsee")) {
				int objId = Integer.parseInt(params[3]);
				Player player = World.getInstance().findPlayer(objId);
				target.getController().notSee(player, true);
			}
		} else if (params[1].equals("mail")) {
			StringBuilder strbld = new StringBuilder("-mails:\n");
			Mailbox mail = target.getMailbox();
			strbld.append("unread = " + mail.getUnreadCount() + "\n");
			Collection<Letter> list = mail.getLetters();
			strbld.append("total = " + list.size() + "\n");
			Iterator<Letter> it = list.iterator();
			while (it.hasNext()) {
				Letter letter = it.next();
				if(letter.getAttachedItem() != null) {
					strbld.append("    sender: " + letter.getSenderName()).append(letter.getAttachedItem().getItemCount()).append("(s) of ").append(ChatUtil.item(letter.getAttachedItem().getItemTemplate().getTemplateId())).append("\n");
				}
				if(letter.getAttachedKinah() > 0) {
					strbld.append("    sender: " + letter.getSenderName() + " kinah: " + letter.getAttachedKinah() + "\n");
				}
			}

			list.clear();
			showAllLines(admin, strbld.toString());
		} else if (params[1].equals("petinv")) {
			StringBuilder strbld = new StringBuilder("-pet inventory:\n");
			Pet pet = target.getPet();
			if (pet == null) {
				strbld.append("no pet found\n");
			} else {
				for (Storage bag : target.getPetBags()) {
					ImmutableList<Item> items = bag.getItemsWithKinah();
					Iterator<Item> it = items.iterator();
					if (items.isEmpty()) {
						strbld.append("none\n");
					} else {
						while (it.hasNext()) {
							Item act = it.next();
							strbld.append("    ").append(act.getItemCount()).append("(s) of ").append(ChatUtil.item(act.getItemTemplate().getTemplateId())).append("\n");
						}
					}
				}
			}

			showAllLines(admin, strbld.toString());
		} else if (params[1].equals("legioninv")) {
			StringBuilder strbld = new StringBuilder("-legion inv:\n");
			Legion legion = target.getLegion();
			if (legion == null) {
				strbld.append("no legion found\n");
			} else {
				ImmutableList<Item> items = legion.getLegionWarehouse().getItemsWithKinah();
				Iterator<Item> it = items.iterator();
				if (items.isEmpty()) {
					strbld.append("none\n");
				} else {
					while (it.hasNext()) {
						Item act = it.next();
						strbld.append("    ").append(act.getItemCount()).append("(s) of ").append(ChatUtil.item(act.getItemTemplate().getTemplateId())).append("\n");
					}
				}
			}

			showAllLines(admin, strbld.toString());
		}

		else {
			admin.sendMsg("bad switch!");
			admin.sendMsg("syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars | knownlist[info|add|remove] | visual[see|notsee]> ");
		}
	}

	private void showAllLines(Player admin, String str) {
		int index = 0;
		String[] strarray = str.split("\n");

		while (index < strarray.length - 20) {
			StringBuilder strbld = new StringBuilder();
			for (int i = 0; i < 20; i++, index++) {
				strbld.append(strarray[index]);
				if (i < 20 - 1) {
					strbld.append("\n");
				}
			}
			admin.sendMsg(strbld.toString());
		}
		int odd = strarray.length - index;
		StringBuilder strbld = new StringBuilder();
		for (int i = 0; i < odd; i++, index++) {
			strbld.append(strarray[index]).append("\n");
		}
		admin.sendMsg(strbld.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects
	 * .player.Player, java.lang.Exception)
	 */
	@Override
	public void onError(Player player, Exception e) {
		player.sendMsg("syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars> ");
	}
}
