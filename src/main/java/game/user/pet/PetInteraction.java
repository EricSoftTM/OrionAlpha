package game.user.pet;

import util.Range;

/**
 *
 * @author Eric
 */
public class PetInteraction {
	private Range level;
	private int friendnessInc;
	private int prob;
	//private List<String> commands, actSuccess, actFail;
	
	public PetInteraction() {
		this.level = new Range();
	}
	
	public int getFriendnessInc() {
		return friendnessInc;
	}
	
	public Range getLevelRange() {
		return level;
	}
	
	public int getProb() {
		return prob;
	}
	
	public void setFriendnessInc(int inc) {
		this.friendnessInc = inc;
	}
	
	public void setProb(int prob) {
		this.prob = prob;
	}
}
