package robostar.robocert.util;

import robostar.robocert.BinaryMessageSet;
import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.MessageSet;
import robostar.robocert.RefMessageSet;
import robostar.robocert.UniverseMessageSet;

/**
 * Performs structural analysis of a set.
 * <p>
 * This lets us acquire approximate information about whether a set is empty, universal, inhabited,
 * and so on.  Such information is useful in rewrite rules.
 *
 * @author Matt Windsor
 */
public class SetAnalyser {

  /**
   * Whether the analyser will analyse through set references.
   */
  private final boolean followsRefs;

  /**
   * Constructs a new SetAnalyser.
   *
   * @param followsRefs whether the analyser will analyse through set references; if false, any such
   *                    set will be classified as unknown.
   */
  public SetAnalyser(boolean followsRefs) {
    super();
    this.followsRefs = followsRefs;
  }

  /**
   * Analyses a message set
   *
   * @param m the set in question.
   * @return an analysis of the set.
   */
  public Analysis analyse(MessageSet m) {
    final var a = new Runner().doSwitch(m);
    if (a == null) {
      throw new IllegalArgumentException(
          "analysis returned null for %s, likely an unsupported object".formatted(m));
    }
    return a;
  }

  /**
   * Switch-based visitor for performing the set analysis.
   */
  private class Runner extends RoboCertSwitch<SetAnalyser.Analysis> {

    @Override
    public Analysis caseExtensionalMessageSet(ExtensionalMessageSet m) {
      if (m == null || m.getMessages().isEmpty()) {
        return Analysis.Unknown;
      }

      return m.getMessages().isEmpty() ? Analysis.Empty : Analysis.Inhabited;
    }

    @Override
    public Analysis caseUniverseMessageSet(UniverseMessageSet m) {
      if (m == null) {
        return Analysis.Unknown;
      }

      return Analysis.Universal;
    }

    @Override
    public Analysis caseRefMessageSet(RefMessageSet m) {
      if (!followsRefs || m == null) {
        return Analysis.Unknown;
      }

      final var nset = m.getSet();
      if (nset == null) {
        return Analysis.Unknown;
      }

      return analyse(nset.getSet());
    }

    @Override
    public Analysis caseBinaryMessageSet(BinaryMessageSet m) {
      if (m == null) {
        return Analysis.Unknown;
      }

      final var l = doSwitch(m.getLhs());
      final var r = doSwitch(m.getRhs());

      return switch (m.getOperator()) {
        case UNION -> union(l, r);
        case INTERSECTION -> intersection(l, r);
        case DIFFERENCE -> difference(l, r);
      };
    }

    private Analysis union(Analysis l, Analysis r) {
      // If either side has all messages, the union also has all messages.
      if (l == Analysis.Universal || r == Analysis.Universal) {
        return Analysis.Universal;
      }

      // If either side has at least one message, so does the union.
      // This works even if the other side is unknown.
      if (l == Analysis.Inhabited || r == Analysis.Inhabited) {
        return Analysis.Inhabited;
      }

      // If both sides are empty, then so is the union.
      if (l == Analysis.Empty && r == Analysis.Empty) {
        return Analysis.Empty;
      }

      // If we get here, we can't make any sound inferences.
      return Analysis.Unknown;
    }

    private Analysis intersection(Analysis l, Analysis r) {
      // If both sides have all messages, the intersection also has all messages.
      if (l == Analysis.Universal && r == Analysis.Universal) {
        return Analysis.Universal;
      }

      // If one side is universal and the other is inhabited, then we know that the intersection
      // contains at least the messages on the inhabited side.
      if (l == Analysis.Universal && r == Analysis.Inhabited) {
        return Analysis.Inhabited;
      }
      if (l == Analysis.Inhabited && r == Analysis.Universal) {
        return Analysis.Inhabited;
      }

      // If either side is empty, then so is the intersection.
      if (l == Analysis.Empty || r == Analysis.Empty) {
        return Analysis.Empty;
      }

      // If we get here, we can't make any sound inferences.
      // For example, we can't tell whether the intersection of two inhabited message sets is
      // inhabited in general, as they could be disjoint.
      return Analysis.Unknown;
    }

    private Analysis difference(Analysis l, Analysis r) {
      // If we are subtracting all messages, we know the result must be empty.
      if (r == Analysis.Universal) {
        return Analysis.Empty;
      }

      // If we are subtracting no messages, we know the minuend's analysis holds unchanged.
      if (r == Analysis.Empty) {
        return l;
      }

      // Otherwise, we can make no inferences:
      // - subtracting inhabited sets from the universe might end up with an empty set if the universe
      //   of messages is finite;
      // - subtracting inhabited sets from each other may or may not result in inhabited or empty
      //   sets;
      // - subtracting unknown sets, or from unknown sets, doesn't in general give us any knowledge.
      return Analysis.Unknown;
    }
  }

  /**
   * Enumeration of possible analyses of a set.
   */
  public enum Analysis {
    /**
     * The message set is known to have no messages.
     * <p>
     * This is never a false positive.  If we are unsure whether a message set is inhabited or not,
     * we return a null analysis.
     */
    Empty,
    /**
     * The message set is known to have at least one message.
     * <p>
     * This may be a pessimistic analysis (eg, the message set may actually be universal).
     */
    Inhabited,
    /**
     * The message set is known to have all messages.
     * <p>
     * This is never a false positive, but we may make false negatives (eg, marking universal sets
     * as inhabited).
     */
    Universal,
    /**
     * We can't infer an analysis on this message set.
     * <p>
     * This is separate from null so as to stop the analyser from trying to chain up the object
     * hierarchy when we return an unknown result.
     */
    Unknown
  }
}
