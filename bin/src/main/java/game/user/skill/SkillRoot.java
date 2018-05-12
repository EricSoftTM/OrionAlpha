package game.user.skill;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arnah
 */
public class SkillRoot {

    private int skillRootID;
    private String bookName;
    private final List<SkillEntry> skills = new ArrayList<>();

    public int getSkillRootID() {
        return skillRootID;
    }

    public void setSkillRootID(int skillRootID) {
        this.skillRootID = skillRootID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public List<SkillEntry> getSkills() {
        return skills;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + skillRootID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SkillRoot))
            return false;
        SkillRoot other = (SkillRoot) obj;
        if (skillRootID != other.skillRootID)
            return false;
        return true;
    }

}
