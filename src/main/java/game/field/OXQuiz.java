package game.field;

import common.BroadcastMsg;
import game.user.User;
import game.user.WvsContext;
import util.Rect;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Eric
 */
public class OXQuiz extends Field {
	private int quizNumber;
	private final Map<Integer, Boolean> questions;
	
	public OXQuiz(int fieldID) {
		super(fieldID);
		
		this.quizNumber = 0;
		this.questions = new HashMap<>();
		
		loadQuizData();
	}
	
	public void banish() {
		if (quizNumber != 0 && getForcedReturnFieldID() != Field.Invalid) {
			if (questions.isEmpty() || !questions.containsKey(quizNumber)) {
				return;
			}
			String area = "x";
			if (questions.get(quizNumber)) {
				area = "o";
			}
			Rect rc = getAreaRect().get(area);
			int eliminated = 0;
			for (User user : getUsers()) {
				if (user.isGM() || rc.ptInRect(user.getCurrentPosition())) {
					user.sendPacket(FieldPacket.onQuiz(true, 0));
				} else {
					user.postTransferField(getForcedReturnFieldID(), "", false);
					eliminated++;
				}
			}
			broadcastPacket(WvsContext.onBroadcastMsg(BroadcastMsg.Notice, String.format(" %d people have been eliminated from the OX Quiz.", eliminated)), false);
			quizNumber = 0;
		}
	}
	
	@Override
	public int getFieldType() {
		return FieldType.OXQuiz;
	}
	
	public int getQuizNumber() {
		return quizNumber;
	}
	
	private void loadQuizData() {
		WzPackage etcDir = new WzFileSystem().init("Etc").getPackage();
		if (etcDir != null) {
			WzProperty quizData = etcDir.getItem("OXQuiz.img");
			if (quizData != null) {
				for (WzProperty quiz : quizData.getChildNodes()) {
					int problem = Integer.parseInt(quiz.getNodeName());
					boolean answer = WzUtil.getBoolean(quiz.getNode("a"), false);
					questions.put(problem, answer);
				}
			}
			etcDir.release();
		}
	}
	
	public void setProblem(boolean question, int number) {
		if (!getAreaRect().containsKey("o") || !getAreaRect().containsKey("x")) {
			return;
		}
		if (question) {
			if (this.quizNumber != 0 && number != 0) {
				return;
			}
		} else {
			if (this.quizNumber == 0 && number != 0) {
				return;
			}
			number = this.quizNumber;
		}
		if (questions.isEmpty() || !questions.containsKey(number)) {
			number = 0;
		}
		this.quizNumber = number;
		broadcastPacket(FieldPacket.onQuiz(question, number), false);
	}
}
