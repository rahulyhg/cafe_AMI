package co.geeksters.hq.events.success;

/**
 * Created by soukaina on 03/12/14.
 */
public class DeleteMemberEvent {
    public boolean inEvent = false;

    public DeleteMemberEvent(){
        this.inEvent = true;
    }
}