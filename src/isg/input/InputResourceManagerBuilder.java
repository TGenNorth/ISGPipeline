/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.broadinstitute.sting.utils.exceptions.UserException;
import scala.actors.threadpool.Arrays;

/**
 *
 * Validation detection:
 * conflicting sample names
 * unpaired fastqs
 * more than one sample in vcf or bam
 * 
 * 
 * @author jbeckstrom
 */
public class InputResourceManagerBuilder {

    private final Map<String, InputResource> resources = new HashMap<String, InputResource>();
    private final Set<InputResourceType> allowedTypes = new HashSet<InputResourceType>();
    private final List<Exception> caughtExceptions = new ArrayList<Exception>();
    private final InputResourceFactoryImpl inputResourceFactory;
    private final FilenameFilter filter = new FilenameFilter() {

        @Override
        public boolean accept(File file, String string) {
            for (InputResourceType type : allowedTypes) {
                if (InputResourceType.filenameEndsWith(string, type.getExtensions())) {
                    return true;
                }
            }
            return false;
        }
    };

    public InputResourceManagerBuilder(InputResourceFactoryImpl inputResourceFactory) {
        this(Arrays.asList(InputResourceType.values()), inputResourceFactory);
    }

    public InputResourceManagerBuilder(Collection<InputResourceType> allowedTypes, InputResourceFactoryImpl inputResourceFactory) {
        this.allowedTypes.addAll(allowedTypes);
        this.inputResourceFactory = inputResourceFactory;
    }

    public void addDir(File dir) {
        for (File f : dir.listFiles(filter)) {
            addFile(f);
        }
    }

    public void addFile(File f) {
        try {
            InputResource<?> inputResource = inputResourceFactory.create(f);
            if (inputResource != null) {
                addInputResource(inputResource);
            }
        } catch (IOException ex) {
            caughtExceptions.add(ex);
        } catch (UserException ex) {
            caughtExceptions.add(ex);
        }
    }

    public void addInputResource(InputResource<?> inputResource) throws UserException {
        if (!allowedTypes.contains(inputResource.type())) {
            throw new IllegalStateException("Attempted to add unallowed resource '" + inputResource + "' of type: " + inputResource.type());
        }
        if (resources.containsKey(inputResource.sampleName())) {
            InputResource<?> duplicate = resources.get(inputResource.sampleName());
            throw new InputResourceValidationExceptions.DuplicateSampleNamesException(inputResource, duplicate);
        }
        resources.put(inputResource.sampleName(), inputResource);
    }

    public InputResourceManager build() throws UserException {
        if (!caughtExceptions.isEmpty()) {
            throw new InputResourceValidationExceptions.CompoundUserException(caughtExceptions);
        }
        return new InputResourceManager(resources);
    }
}
