package dragion.simulations.experimental;

import java.nio.file.Path;

import org.tomlj.TomlTable;

import dragion.simulations.TomlInputCarriers;
import lestat.surfaces.BiasedConductor;
import lestat.surfaces.ElectrostaticObject;
import lestat.surfaces.FloatingConductor;

public class TomlInputExperimental extends TomlInputCarriers
{

	static final String FOLLOWING = "following";
	
//	final FloatingConstraints followingConstraints = new FloatingConstraints();
//	final Map<FloatingConductor, FloatingConductor> followingPairs = new HashMap<>();

//	public FloatingConstraints getFollowingConstraints()
//	{
//		return followingConstraints;
//	}
	
//	public Map<FloatingConductor, FloatingConductor> getFollowingPairs()
//	{
//		return followingPairs;
//	}

	
//	static class LeaderFollower extends FloatingConductor
//	{
//		final boolean[] collect;
//		
//		public LeaderFollower(vtkPolyData leader, vtkPolyData follower)
//		{
//			super(VtkUtil.aggregate(leader, follower));
//			
//			vtkIntArray srcidarr = (vtkIntArray)getPolyData().GetCellData().GetArray(VtkUtil.SRCID_ARRAY_NAME);
//			collect = new boolean[getNumberOfFaces()];
//			for (int i = 0; i < getNumberOfFaces(); i++)
//			{
//				collect[i] = (srcidarr.GetValue(i) == 0);	// follower is source id 1; leader is source id 0
//			}
//		}
//		
//		@Override
//		public void accumulateCharge(Particle p, int f)
//		{
//			if (collect[f])
//			{
//				super.accumulateCharge(p, f);
//			}
//		}
//		
//		
//	}
	
	FloatingConductor leader;
	BiasedConductor follower;

	public FloatingConductor getLeader()
	{
		return leader;
	}
	
	public BiasedConductor getFollower()
	{
		return follower;
	}
	
	public TomlInputExperimental(Path inputfile)
	{
		super(inputfile);

		TomlTable allObjectSpecs = parseResult.getTable(OBJECTS);
		//
		for (String followerName : allObjectSpecs.keySet())
		{
			TomlTable objectSpec = allObjectSpecs.getTable(followerName);

			if (objectSpec.contains(FOLLOWING))
			{
				String leaderName = objectSpec.getString(FOLLOWING);

				if (followerName == leaderName)
				{
					throw new Error("Object '" + followerName + "' cannot follow its own voltage");
				}

				if (leader != null)
				{
					throw new Error("Only one leader/follower pair is allowed, for now");
				}
				
				ElectrostaticObject leaderObj = objects.get(leaderName);
				ElectrostaticObject followerObj = objects.get(followerName);

				if (!(leaderObj instanceof FloatingConductor))
				{
					throw new Error("Voltage follower '" + followerName + "' cannot follow '" + leaderName + "' which is a " + leaderObj.getClass().getName() + "; it can only follow a " + FloatingConductor.class.getName());
				}

				if (!(followerObj instanceof BiasedConductor))
				{
					throw new Error("Voltage follower '" + followerName + "' must be a " + BiasedConductor.class.getName());
				}
				
				leader = (FloatingConductor) leaderObj;
				follower = (BiasedConductor) followerObj;
				
//				followingConstraints.add((FloatingConductor)leaderObj, new Supplier<Double>()
//				{
//					final Vector3D leaderCenter = VtkUtil.pointCenter(leaderObj.getPolyData());
//					final BiasedConductor follower = (BiasedConductor)followerObj;
//					
//					@Override
//					public Double get()
//					{
//						// set follower bias to leader potential
//						double phiLeader = solver.synthesizeElectricPotential(leaderCenter);
//						follower.setPotential(phiLeader);
//						// this, in turn, induces charge on the follower; we must correct for the corresponding shift in leader potential 
//						// ... the correction is done by the caller, here we just return the unperturbed floating potential
//						return phiLeader;
//					}
//				});

//				followingPairs.put((FloatingConductor)leaderObj, (FloatingConductor)followerObj);

//				objects.remove(leader);
//				objects.remove(follower);
//				
//				objects.put(leaderName, new LeaderFollower(leader.getPolyData(), follower.getPolyData()));
				
//				follower.disableAccumulation();
				
			}
		}

	}

}
