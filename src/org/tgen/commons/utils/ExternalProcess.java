package org.tgen.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

public class ExternalProcess implements Runnable{

	private String[] cmd;
	private OutputStream os;
	private int exit = -1;
        private File workingDirectory;
	
        public ExternalProcess(String[] cmd, File workingDirectory){
		this.cmd = cmd;
                this.workingDirectory = workingDirectory;
	}
        
	public ExternalProcess(String[] cmd){
		this.cmd = cmd;
	}
	
	public ExternalProcess(String[] cmd, OutputStream os){
		this.cmd = cmd;
		this.os = os;
	}
	
	public boolean completed(){
		return exit==0;
	}
	
	@Override
	public void run() {
		try {

			ProcessBuilder pb = new ProcessBuilder(cmd);
                        if(workingDirectory!=null){
                            pb.directory(workingDirectory);
                        }
			pb.redirectErrorStream(true);
			Process p = pb.start();
			
			if(os!=null){
				InputStream is = p.getInputStream();
			    byte buf[]=new byte[1024];
			    int len;
			    while((len=is.read(buf))>0){
				    os.write(buf,0,len);
			    }
				is.close();
				os.close();
				
			}
			
			exit = p.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		File solsnp = new File("/Users/jbeckstrom/Documents/tgen/solsnp/SolSNP-1.1/SolSNP.jar");
		File input = new File("/Volumes/isilon.tgen.org/tnorth/sbeckstr/Anthrax/Bfast/vsAmesA_genomes/A0006/unique/bfast.reported.file.A0006_merged.bam");
		File output = new File("test/out.gff");
		File refSeq = new File("/Volumes/isilon.tgen.org/tnorth/sbeckstr/Anthrax/fastas/to_use/AmesA.fasta");
		String[] cmd = CommandFactory.generateSolSnpCommand(solsnp, input, output, refSeq);
		ExternalProcess ep = new ExternalProcess(cmd, System.out);
		ep.run();
	}

}
