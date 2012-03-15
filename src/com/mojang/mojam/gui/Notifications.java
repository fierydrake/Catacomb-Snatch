package com.mojang.mojam.gui;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.screen.Screen;

public class Notifications {
	public enum For { CHARACTER, TEAM, OPPOSING_TEAMS, ALL };

	public class Note {
		public String message;
		public int life;
		public GameCharacter sourceCharacter;
		public Team sourceTeam;
		public For messageFor;

		public Note(String message, int life, GameCharacter sourceCharacter, Team sourceTeam, For messageFor) {
			this.message = message;
			this.life = life;
			this.sourceCharacter = sourceCharacter;
			this.sourceTeam = sourceTeam;
			this.messageFor = messageFor; 
		}

		public void tick() {
			if (life-- <= 0) {
				Notifications.getInstance().notes.remove(this);
			}
		}
	}

	private static Notifications instance = null;

	private List<Note> notes = new CopyOnWriteArrayList<Note>();

	public void add(String message, GameCharacter sourceCharacter, Team sourceTeam, For messageFor) {
		add(message, 150, sourceCharacter, sourceTeam, messageFor);
	}

	public void add(String message, int life, GameCharacter sourceCharacter, Team sourceTeam, For messageFor) {
		notes.add(new Note(message, life, sourceCharacter, sourceTeam, messageFor));
	}

	private boolean shouldRenderNote(Note note, GameView view) {
		return (note.messageFor == For.ALL 
				|| (note.messageFor == For.TEAM && note.sourceTeam == view.getPlayer().team ) 
				|| (note.messageFor == For.OPPOSING_TEAMS && note.sourceTeam != view.getPlayer().team ) 
				|| (note.messageFor == For.CHARACTER && note.sourceCharacter == view.getPlayer().getCharacter()));
	}
	
	public void render(Screen screen, GameView view) {
		Iterator<Note> it = notes.iterator();
		int i = 0;
		while (it.hasNext()) {
			i += 1;
			Note note = it.next();
			if (shouldRenderNote(note, view)) {
				int stringWidth = Font.defaultFont().calculateStringWidth(note.message);
				Font.defaultFont().draw(screen, note.message, (screen.w / 2) - (stringWidth / 2), screen.h / 5 + (i * 16));
			}
		}
	}

	public void tick() {
		for (Note n : notes) {
			n.tick();
		}
	}

	private Notifications() {
	}

	public static synchronized Notifications getInstance() {
		if (instance == null) {
			instance = new Notifications();
		}

		return instance;
	}

}
