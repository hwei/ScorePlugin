package me.hwei.bukkit.scoreplugin.data;

import java.util.List;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlUpdate;

public class Storage {
	protected static Storage instance = null;
	public static Storage GetInstance() {
		return instance;
	}
	public static void SetUp(EbeanServer database) {
		instance = new Storage(database);
	}
	
	protected Storage(EbeanServer database) {
		this.database = database;
	}
	
	public Work load(Work work) {
		return this.database
			.find(Work.class)
			.where()
			.eq("world", work.getWorld())
			.eq("pos_x", work.getPos_x())
			.eq("pos_y", work.getPos_y())
			.eq("pos_z", work.getPos_z())
			.findUnique();
	}
	
	public void save(Work work) {
		this.database.save(work);
	}
	
	public void delete(Work work) {
		String sql = "delete from scores where work_id = :work_id";
		SqlUpdate delete = database.createSqlUpdate(sql);
		delete.setParameter("work_id", work.getWork_id());
		database.execute(delete);
		database.delete(work);
	}
	
	public List<Work> loadOpenWorkList(int pageSize) {
		return this.database
				.find(Work.class)
				.where()
				.eq("reward", null)
				.orderBy("work_id desc")
				.setMaxRows(pageSize)
				.findList();
	}
	
	public List<Work> loadClosedWorkList(int pageSize) {
		return this.database
				.find(Work.class)
				.where()
				.ne("reward", null)
				.orderBy("work_id desc")
				.setMaxRows(pageSize)
				.findList();
	}
	
	public Work loadOpenWorkAt(int index) {
		return this.database
				.find(Work.class)
				.where()
				.eq("reward", null)
				.orderBy("work_id desc")
				.setFirstRow(index)
				.setMaxRows(1)
				.findUnique();
	}
	
	public Score load(int workId, String viewerName) {
		return this.database
			.find(Score.class)
			.where()
			.eq("viewer", viewerName)
			.eq("work_id", workId)
			.findUnique();
	}
	public void save(Score score) {
		this.database.save(score);
	}
	public void clearScore(int work_id) {
		String sql = "delete from scores where work_id = :work_id";
		SqlUpdate delete = this.database.createSqlUpdate(sql);
		delete.setParameter("work_id", work_id);
		this.database.execute(delete);
	}
	
	public int scoreCount(int workId) {
		return this.database
			.find(Score.class)
			.where()
			.eq("work_id", workId)
			.findRowCount();
	}
	
	public ScoreAggregate scoreAggregate(int work_id) {
		int scoreCount = this.database
				.find(Score.class)
				.where()
				.eq("work_id", work_id)
				.findRowCount();
		if(scoreCount <= 0) {
			return null;
		}
		String sql
		= "select avg(score) as average, min(score) as min, max(score) as max, sum(score) as sum "
		+ "from scores";
		RawSql rawSql = RawSqlBuilder.parse(sql).create();
		return this.database
			.find(ScoreAggregate.class)
			.setRawSql(rawSql)
			.where()
			.eq("work_id", work_id)
			.findUnique();
	}
	
	public List<Score> loadScoreList(int work_id) {
		return this.database
				.find(Score.class)
				.where()
				.eq("work_id", work_id)
				.findList();
	}
	
	public void saveScoreList(List<Score> scoreList) {
		this.database.save(scoreList);
	}
	
	protected EbeanServer database;
}
