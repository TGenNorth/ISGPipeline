package org.tgen.sol;

import net.sf.samtools.SAMFileReader;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Alexis
 * Date: Sep 13, 2010
 * Time: 7:10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class AllelicBalancer {

	private File input;
	private SAMFileReader input_reader;

	public AllelicBalancer (File file) {
		input_reader = new SAMFileReader(file);
		//System.out.println(input_reader.getFileHeader().getSequenceDictionary().getSequences().size());
	}

	private void GenerateRandomLocus () {

	}
}
