package es.archetyp.archetypes2.gui.archetypes.detail;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.vaadin.data.Item;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;

@UIScope
@SpringComponent
// see https://dev.vaadin.com/browser/svn/versions/6.0/src/com/vaadin/tests/TreeFilesystem.java
public class AchetypeSourceTree extends Panel {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ApplicationEventPublisher publisher;

	public AchetypeSourceTree() {
		setCaption("Generated example");
	}

	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeDetailChangedEvent e) {
		final Archetype archetype = e.getArchetype();
		if (!archetype.getLocalDir().isPresent()) {
			setContent(new Label("No source tree available"));
		} else {
			setContent(createDirectoryTree(new File(archetype.getLocalDir().get())));
		}
	}

	private Tree createDirectoryTree(final File baseDir) {
		final Tree tree = new Tree();
		tree.addExpandListener(e -> {
			final Item i = tree.getItem(e.getItemId());
			if (!tree.hasChildren(i)) {
				populateNode(tree, e.getItemId().toString(), baseDir, e.getItemId());
			}
		});
		tree.addItemClickListener(i -> {
			String file = (String) i.getItemId();
			Object p = tree.getParent(i.getItemId());
			while (p != null) {
				file = p + "/" + file;
				p = tree.getParent(p);
			}
			publisher.publishEvent(new ArchetypeExampleFileSelectedEvent(new File(baseDir, file)));
		});
		populateNode(tree, "", baseDir, null);
		return tree;
	}

	private void populateNode(final Tree tree, final String file, final File baseDir, final Object parent) {
		String p = (String) parent;
		String dir = "";
		while (p != null) {
			dir = p + File.separator + dir;
			p = (String) tree.getParent(p);
		}
		final File subdir = new File(baseDir, dir);
		final File[] files = subdir.listFiles();
		Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(final File o1, final File o2) {
                if (o1.isDirectory())
                    return o2.isDirectory() ? o1.compareTo(o2) : -1;
                else if (o2.isDirectory())
                    return 1;
                return o1.compareTo(o2);
            }
        });
		for (final File subFile : files) {
			final String path = subFile.toString().replace(subdir.toString(), "").replace("\\", "").replace("/", "");
			tree.addItem(path);
			if (parent != null) {
				tree.setParent(path, parent);
			}
			if (subFile.isDirectory() && subFile.canRead()) {
				tree.setChildrenAllowed(path, true);
			} else {
				tree.setChildrenAllowed(path, false);
			}
		}
	}
}
