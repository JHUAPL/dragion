package dragion.physics;

import java.util.function.BiFunction;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface SourceFunction extends BiFunction<Vector3D, Double, Double>
{

}
