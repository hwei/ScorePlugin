package me.hwei.bukkit.scoreplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import me.hwei.bukkit.scoreplugin.data.Storage;
import me.hwei.bukkit.scoreplugin.data.Work;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.Marker;

public class ScoreDynmap implements Listener {
	static protected ScoreDynmap Instance = null;
	
	static void Update() {
		if(Instance == null)
			return;
		Instance.update();
	}
	
	static Listener getSetupListerner() {
		return new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.MONITOR)
		    public void onPluginEnable(PluginEnableEvent event) {
		        Plugin p = event.getPlugin();
		        String name = p.getDescription().getName();
		        if(name.equals("dynmap")) {
		        	Setup(p);
		        }
		    }
		};
	}
	
	public static void Setup(Plugin dynmap) {
		DynmapCommonAPI dynmapCoreAPI = (DynmapCommonAPI)dynmap;
		Instance = new ScoreDynmap(dynmapCoreAPI.getMarkerAPI());
		Instance.update();
	}
	
	protected ScoreDynmap(MarkerAPI markerAPI) {
		this.layers = new ArrayList<Layer>();
		if(ScoreConfig.getDynmapDisplayOpen())
			this.layers.add(new OpenScoreLayer(markerAPI));
		if(ScoreConfig.getDynmapDisplayClosed())
			this.layers.add(new ClosedScoreLayer(markerAPI));
	}
	
	protected List<Layer> layers;
	
	protected void update() {
		for(Layer layer : this.layers) {
			layer.update();
		}
	}
	
	private class OpenScoreLayer extends Layer {

		public OpenScoreLayer(MarkerAPI markerAPI) {
			MarkerSet markerSet = markerAPI.getMarkerSet("score.open");
			if(markerSet == null) {
				markerSet = markerAPI.createMarkerSet(
						"score.open", "Score-open", null, false);
			} else {
				markerSet.setMarkerSetLabel("Score-open");
			}
			MarkerIcon markerIcon = markerAPI.getMarkerIcon("star");
			this.Init(markerSet, markerIcon);
		}

		@Override
		protected List<MarkerData> getMarkers() {
			List<Work> works = Storage.GetInstance().loadOpenWorkList(1000);
			ArrayList<MarkerData> markers = new ArrayList<MarkerData>(works.size());
			for(int i=0; i<works.size(); ++i) {
				Work work = works.get(i);
				MarkerData markerData = new MarkerData();
				markerData.id = work.getWork_id();
				markerData.worldName = work.getWorld();
				markerData.locX = work.getPos_x();
				markerData.locY = work.getPos_y();
				markerData.locZ = work.getPos_z();
				markerData.label = work.getName();
				markerData.description = String.format(
						"<table><caption>Score</caption><tbody><tr><td>Name</td><td><strong>%s</strong></td></tr><tr><td>Author</td><td><strong>%s</strong><img src=\"tiles/faces/16x16/%s.png\"></td></tr><tr><td>TP</td><td><strong>%d</strong></td></tr></tbody></table>",
						work.getName(), work.getAuthor(), work.getAuthor(), i);
				markers.add(markerData);
			}
			return markers;
		}
		
	}
	
	private class ClosedScoreLayer extends Layer {

		public ClosedScoreLayer(MarkerAPI markerAPI) {
			MarkerSet markerSet = markerAPI.getMarkerSet("score.closed");
			if(markerSet == null) {
				markerSet = markerAPI.createMarkerSet(
						"score.closed", "Score-closed", null, false);
			} else {
				markerSet.setMarkerSetLabel("Score-closed");
			}
			MarkerIcon markerIcon = markerAPI.getMarkerIcon("goldstar");
			this.Init(markerSet, markerIcon);
		}

		@Override
		protected List<MarkerData> getMarkers() {
			List<Work> works = Storage.GetInstance().loadClosedWorkList(1000);
			ArrayList<MarkerData> markers = new ArrayList<MarkerData>(works.size());
			for(int i=0; i<works.size(); ++i) {
				Work work = works.get(i);
				MarkerData markerData = new MarkerData();
				markerData.id = work.getWork_id();
				markerData.worldName = work.getWorld();
				markerData.locX = work.getPos_x();
				markerData.locY = work.getPos_y();
				markerData.locZ = work.getPos_z();
				markerData.label = work.getName();
				markerData.description = String.format(
						"<table><caption>Score</caption><tbody><tr><td>Name</td><td><strong>%s</strong></td></tr><tr><td>Author</td><td><strong>%s</strong><img src=\"tiles/faces/16x16/%s.png\"></td></tr><tr><td>Score</td><td><strong>%.2f</strong></td></tr></tbody></table>",
						work.getName(), work.getAuthor(), work.getAuthor(), work.getScore());
				markers.add(markerData);
			}
			return markers;
		}
		
	}
	
	private abstract class Layer {
		protected Layer() { }
		protected void Init(MarkerSet markerSet, MarkerIcon markerIcon) {
			this.markerSet = markerSet;
			this.markerIcon = markerIcon;
			this.oldMarkers = new TreeMap<Integer, Marker>();
		}
		private MarkerSet markerSet;
		private MarkerIcon markerIcon;
		private Map<Integer, Marker> oldMarkers;
		protected abstract List<MarkerData> getMarkers();
		public void update() {
			List<MarkerData> markers = this.getMarkers();
			Map<Integer, Marker> newMarkers = new TreeMap<Integer, Marker>();
			for(MarkerData markerData : markers) {
				Marker marker = this.oldMarkers.remove(markerData.id);
				if(marker == null) {
					marker = this.markerSet.createMarker(
							Integer.toString(markerData.id),
							markerData.label,
							markerData.worldName,
							markerData.locX,
							markerData.locY,
							markerData.locZ,
							this.markerIcon, false);
					marker.setDescription(markerData.description);
				} else {
					marker.setLocation(
							markerData.worldName,
							markerData.locX,
							markerData.locY,
							markerData.locZ);
					marker.setLabel(markerData.label);
					marker.setMarkerIcon(this.markerIcon);
					marker.setDescription(markerData.description);
				}
				newMarkers.put(markerData.id, marker);
			}
			for(Marker marker : this.oldMarkers.values()) {
				marker.deleteMarker();
			}
			
			this.oldMarkers.clear();
			this.oldMarkers = newMarkers;
		}
	}
	
	private class MarkerData {
		public String label;
		public String description;
		public int id;
		public String worldName;
		public int locX;
		public int locY;
		public int locZ;
	}
}
