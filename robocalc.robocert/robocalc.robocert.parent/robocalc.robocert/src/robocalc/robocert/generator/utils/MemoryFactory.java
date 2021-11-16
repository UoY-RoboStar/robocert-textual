package robocalc.robocert.generator.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.xtext.EcoreUtil2;

import com.google.inject.Inject;

import circus.robocalc.robochart.Type;
import robocalc.robocert.generator.utils.name.BindingNamer;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.Sequence;

/**
 * Extracts memory definitions from sequences.
 *
 * @author Matt Windsor
 */
public class MemoryFactory {
	@Inject private BindingNamer bnx;
	@Inject private BindingTypeFinder btf;
	
	/**
	 * Type of processed memory definitions.
	 *
	 * @author Matt Windsor
	 */
	public record Memory(Sequence parent, List<Memory.Slot> slots) {
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
	
	/**
	 * Determines whether this {@link Sequence} will have a memory generated.
	 *
	 * Only sequences with bindings produce memories.
	 *
	 * @param s  the sequence in question.
	 * @return  whether a call to buildMemories with the sequence will yield a
	 *          {@link Memory} for it.
	 */
	public boolean hasMemory(Sequence s) {
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
	public Stream<Memory> buildMemories(Stream<Sequence> sequences) {
		return sequences.map((x) -> build(x)).filter((x) -> !x.slots().isEmpty());
	}
	
	private Memory build(Sequence s) {
		if (s == null || s.getName() == null) {
			throw new NullPointerException("expected a named, non-null sequence here");
		}
		
		var slots = getBindings(s).stream().map((x) -> buildSlot(x)).collect(Collectors.toUnmodifiableList());
		return new Memory(s, slots);
	}
	
	private Memory.Slot buildSlot(Binding b) {
		return new Memory.Slot(b, bnx.getUnambiguousName(b), btf.getType(b));
	}
	
	private List<Binding> getBindings(Sequence s) {
		return EcoreUtil2.eAllOfType(s, Binding.class);
	}
}
