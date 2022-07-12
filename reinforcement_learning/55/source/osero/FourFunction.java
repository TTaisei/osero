package source.osero;

/**
 * Function interface for explore.
 */
@FunctionalInterface
public interface FourFunction {
    public abstract double getScore(long board[], boolean nowTurn, boolean turn, int num);
}