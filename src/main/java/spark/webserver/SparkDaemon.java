package spark.webserver;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

public abstract class SparkDaemon implements Daemon{

	private static final Logger logger = LoggerFactory.getLogger(SparkDaemon.class);
	protected Spark sparkInstance;

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		sparkInstance = new Spark();
	}

	@Override
	public void start() throws Exception {
		logger.info("SPARK STARTED");
		sparkInstance.init();
	}

	@Override
	public void stop() throws Exception {
		logger.info("SPARK STOPPED");
		sparkInstance.stop();
	}

	@Override
	public void destroy() {
		//singASong();
	}

}
