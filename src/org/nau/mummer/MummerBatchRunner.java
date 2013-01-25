package org.nau.mummer;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class MummerBatchRunner implements Runnable{

	private final MummerEnv env;
	private final Collection<File> fastas;
	private final Collection<File> references;
        private final boolean maxmatch = false;
	
	public MummerBatchRunner(Collection<File> fastas, Collection<File> references, MummerEnv env){
		this.fastas = fastas;
		this.references = references;
		this.env = env;
	}

	@Override
	public void run() {
		for(File fasta: fastas){
			for(File reference: references){
				MummerRunnerFactory.createMummerRunner(reference, fasta, maxmatch, env).run();
			}
		}
	}
	
}
