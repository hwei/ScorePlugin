package me.hwei.bukkit.scoreplugin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.YamlConfiguration;


public class LanguageManager {
	protected static LanguageManager instance = null;
	public static LanguageManager GetInstance() {
		return instance;
	}
	static protected File LangFile = null;
	public static void Setup(File langFile) {
		LangFile = langFile;
		if(!langFile.exists()) {
			CreateDefault();
		}
		instance = new LanguageManager();
		instance.load();
	}
	public static void Reload() {
		if(instance == null) {
			return;
		}
		if(!LangFile.exists()) {
			CreateDefault();
		}
		instance.load();
	}
	protected static void CreateDefault() {
		try {
			FileOutputStream out = new FileOutputStream(LangFile);

			File jarloc = new File(LanguageManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            if(!jarloc.isFile()) {
                throw new Exception();
            }
            JarFile jar = new JarFile(jarloc);
            JarEntry entry = jar.getJarEntry("language.yml");
            if(!jarloc.isFile()) {
                throw new Exception();
            }
			InputStream in = jar.getInputStream(entry);
			byte[] tempbytes = new byte[512];
            int readbytes;
            readbytes = in.read(tempbytes,0,512);
            while(readbytes>-1) {
                out.write(tempbytes,0,readbytes);
                readbytes = in.read(tempbytes,0,512);
            }
            out.close();
            in.close();
		} catch (Exception e) {
			OutputManager.GetInstance().toConsole().output("Can not write default language.yml!");
		}
	}
	protected Map<String, String> phrases;
	protected LanguageManager() {
	}
	protected void load() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(LangFile);
		this.phrases = new HashMap<String, String>();
		Iterator<Entry<String,Object>> it = config.getValues(false).entrySet().iterator();
		Pattern p = Pattern.compile("&|&&");
		while(it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			Matcher m = p.matcher(entry.getValue().toString());
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				if(m.group().equals("&")) {
					m.appendReplacement(sb, "\u00A7");
				} else if(m.group().equals("&&")) {
					m.appendReplacement(sb, "&");
				}
			}
			m.appendTail(sb);
			phrases.put(entry.getKey(), sb.toString());
		}
	}
	public String getPhrase(String key) {
		if(this.phrases.containsKey(key)) {
			return this.phrases.get(key);
		} else {
			return "<missing language, key: " + key + ">";
		}
	}
}
