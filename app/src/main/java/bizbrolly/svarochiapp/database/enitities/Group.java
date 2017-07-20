package bizbrolly.svarochiapp.database.enitities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import bizbrolly.svarochiapp.database.AppDatabase;

/**
 * Created by Ayush on 03/05/17.
 */
@Table(database = AppDatabase.class)
public class Group extends BaseModel {

    private static final int TYPE_OOD = 0;
    private static final int TYPE_CCT = 1;
    private static final int TYPE_RGB = 2;

    @PrimaryKey(autoincrement = true)
    private int groupId;

    @Unique
    @Column
    private String groupName;

    // Denotes the lowest features allowed for this group
    @Column
    private String groupType;

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Group){
            return groupId == ((Group) obj).groupId;
        } else if (obj instanceof Integer){
            return groupId == (Integer) obj;
        }
        return super.equals(obj);
    }
}
