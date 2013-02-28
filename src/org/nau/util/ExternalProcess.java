package org.nau.util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import net.sf.picard.PicardException;
import net.sf.picard.util.Log;

public class ExternalProcess {

    private static final Log log = Log.getInstance(ExternalProcess.class);
    private static final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {

        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, "ProcessExecutor Thread");
        }
    });

    public static int execute(String[] cmd) {
        return execute(cmd, null);
    }

    public static int execute(String[] cmd, File wd) {
        return execute(cmd, wd, new LogInfoProcessOutputReader(), new LogErrorProcessOutputReader());
    }
    
    public static int execute(String[] cmd, File wd, ProcessOutputHandler outputHandle) {
        return execute(cmd, wd, outputHandle, new LogErrorProcessOutputReader());
    }

    public static int execute(String[] cmd, File wd, ProcessOutputHandler outputHandler, ProcessOutputHandler errorHandler) {
        System.out.println("executing: "+net.sf.samtools.util.StringUtil.join(" ", cmd));
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(wd);
            Process p = pb.start();

            errorHandler.setInputStream(p.getErrorStream());
            outputHandler.setInputStream(p.getInputStream());
            final Future<?> stderrReader = executorService.submit(errorHandler);
            outputHandler.run();
            // wait for stderr reader to be done
            stderrReader.get();

            return p.waitFor();
        } catch (Throwable ex) {
            throw new PicardException("Unexpected exception executing [" + net.sf.samtools.util.StringUtil.join(" ", cmd) + "]", ex);
        }
    }

    private static class LogErrorProcessOutputReader extends ProcessOutputHandlerLineReader {

        @Override
        protected void write(final String message) {
            log.error(message);
        }
    }

    private static class LogInfoProcessOutputReader extends ProcessOutputHandlerLineReader {

        @Override
        protected void write(final String message) {
            log.info(message);
        }
    }

}
