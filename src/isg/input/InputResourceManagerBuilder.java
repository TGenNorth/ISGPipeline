/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class InputResourceManagerBuilder {

    private final List<InputResourceFactory> factories = new ArrayList<InputResourceFactory>();
    private final Map<String, InputResource> resources = new HashMap<String, InputResource>();

    public void addFactory(InputResourceFactory factory) {
        factories.add(factory);
    }

    public void addDir(File dir) {
        for (File f : dir.listFiles()) {
            addFile(f);
        }
    }

    public void addFile(File f) {
        for (InputResourceFactory factory : factories) {
            if (factory.isResourceType(f)) {
                InputResource resource = factory.create(f);
                if (resource != null) {
                    addInputResource(resource);
                }
            }
        }
    }

    public void addInputResource(InputResource resource) {
        if (resources.containsKey(resource.sampleName())) {
            throw new IllegalArgumentException("A resource with sample name '" + resource.sampleName() + "' already exists!");
        }
        resources.put(resource.sampleName(), resource);
    }

    public InputResourceManager build() {
        return new InputResourceManager(resources);
    }
}
