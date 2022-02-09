package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.Type;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.generator.utils.name.BindingNamer;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.Interaction;

/**
 * Extracts memory definitions from sequences.
 *
 * @author Matt Windsor
 */
public record MemoryFactory(BindingNamer bnx, BindingTypeFinder btf) {

	/**
	 * Constructs a MemoryFactory.
	 * @param bnx a binding namer.
	 * @param btf a binding type finder.
	 */
	@Inject
	public MemoryFactory {
		Objects.requireNonNull(bnx);
		Objects.requireNonNull(btf);
	}

	/**
	 * Determines whether this {@link Interaction} will have a memory generated.
	 *
	 * Only sequences with bindings produce memories.
	 *
	 * @param s  the sequence in question.
	 * @return  whether a call to buildMemories with the sequence will yield a
	 *          {@link Memory} for it.
	 */
	public boolean hasMemory(Interaction s) {
		// TODO(@MattWindsor91): is there a more efficient way of doing this?
		return !getBindings(s).isEmpty();
	}
	
	/**
	 * Builds a stream of memories, given a stream of sequences.
	 *
	 * Not every sequence will yield a memory; only those with bindings.
	 *
	 * @param sequences the stream of sequences to feed into the build.
	 * @return a stream of memories for the given sequences.
	 */
	public Stream<Memory> buildMemories(Stream<Interaction> sequences) {
		return sequences.map(this::build).filter((x) -> !x.slots().isEmpty());
	}
	
	private Memory build(Interaction s) {
		if (s == null || s.getName() == null) {
			throw new NullPointerException("expected a named, non-null sequence here");
		}

		final var slots = getBindings(s).stream().map(this::buildSlot).toList();
		return new Memory(s, slots);
	}
	
	private Memory.Slot buildSlot(Binding b) {
		return new Memory.Slot(b, bnx.getUnambiguousName(b), btf.getType(b));
	}
	
	private List<Binding> getBindings(Interaction s) {
		return EcoreUtil2.eAllOfType(s, Binding.class);
	}
	
	/**
	 * Type of processed memory definitions.
	 *
	 * @author Matt Windsor
	 */
	public record Memory(Interaction parent, List<Memory.Slot> slots) {
		public Memory {
			Objects.requireNonNull(parent);
			Objects.requireNonNull(slots);
		}

		public record Slot(Binding binding, String unambiguousName, Type type) {
			public Slot {
				Objects.requireNonNull(binding);
				Objects.requireNonNull(unambiguousName);
				Objects.requireNonNull(type);
			}
		}

	}
}
