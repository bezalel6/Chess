package ver14.SharedClasses.Sync;

/*
 * SyncableItem -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/*
 * SyncableItem -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * SyncableItem
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

public interface SyncableItem {

    default SyncableItem getSyncableItem() {
        return this;
    }

    String ID();
}
