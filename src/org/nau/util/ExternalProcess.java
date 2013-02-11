package org.nau.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        return execute(cmd, null, null);
    }

    public static int execute(String[] cmd, File wd) {
        return execute(cmd, wd, null);
    }

    public static int execute(String[] cmd, File wd, File out) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(wd);
            Process p = pb.start();

            final LogErrorProcessOutputReader err = new LogErrorProcessOutputReader(p.getErrorStream());
            final Future<?> stderrReader = executorService.submit(err);
            final ProcessOutputReader stdout = getOutput(out, p);
            stdout.run();
            // wait for stderr reader to be done
            stderrReader.get();

            return p.waitFor();
        } catch (Throwable ex) {
            throw new PicardException("Unexpected exception executing [" + net.sf.samtools.util.StringUtil.join(" ", cmd) + "]", ex);
        }
    }

    private static ProcessOutputReader getOutput(File out, Process p) throws IOException {
        if (out != null) {
            return new WriteFileProcessOutputReader(p.getInputStream(), out);
        } else {
            return new LogInfoProcessOutputReader(p.getInputStream());
        }
    }

    /**
     * Runnable that reads off the given stream and logs it somewhere.
     */
    private static abstract class ProcessOutputReader implements Runnable {

        private final BufferedReader reader;

        public ProcessOutputReader(final InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream));
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    write(line);
                }
            } catch (IOException e) {
                throw new PicardException("Unexpected exception reading from process stream", e);
            } finally {
                close();
            }
        }

        protected abstract void write(String message);

        protected void close() {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ExternalProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static class LogErrorProcessOutputReader extends ProcessOutputReader {

        public LogErrorProcessOutputReader(final InputStream stream) {
            super(stream);
        }

        @Override
        protected void write(final String message) {
            log.error(message);
        }
    }

    private static class LogInfoProcessOutputReader extends ProcessOutputReader {

        public LogInfoProcessOutputReader(final InputStream stream) {
            super(stream);
        }

        @Override
        protected void write(final String message) {
            log.info(message);
        }
    }

    private static class WriteFileProcessOutputReader extends ProcessOutputReader {

        private final PrintWriter pw;

        public WriteFileProcessOutputReader(final InputStream stream, final File f) throws IOException {
            super(stream);
            pw = new PrintWriter(new FileWriter(f));
        }

        @Override
        protected void write(final String message) {
            pw.println(message);
        }

        @Override
        protected void close() {
            super.close();
            pw.close();
        }
    }
}
