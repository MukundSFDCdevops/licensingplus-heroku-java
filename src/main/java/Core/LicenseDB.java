package Core;

import Core.Nipr.LicenseInternal;
import Core.Utils.CalenderUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by vthiruvengadam on 8/10/16.
 */
public class LicenseDB {

    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();
    private static Map<String, LicenseInternal> unprocessedLicenses = new HashMap<String, LicenseInternal>();
    private static Map<String, GregorianCalendar> pendingNiprSyncDates = new HashMap<String, GregorianCalendar>();
    private static GregorianCalendar lastSuccessfullSync = null;
    private static UUID resyncTriggerId = UUID.randomUUID();
    private static Thread reconcilerThread = null;

    static  {
        pendingNiprSyncDates = CalenderUtils.getLastNDays(Configuration.GetResyncDaysCount());
    }

    public static void setReconcilerThread(Thread aInThread) {
        reconcilerThread = aInThread;
    }

    public static UUID getResyncTriggerId() {

        readLock.lock();
        try {
            return UUID.fromString(resyncTriggerId.toString());
        }
        finally {
            readLock.unlock();
        }
    }

    public static void triggerResync() {
        System.out.println("LicenseDB: Triggering Resync ");
        writeLock.lock();
        try {
            resyncTriggerId = UUID.randomUUID();
            System.out.println("LicenseDB: Triggered Resync ID " + resyncTriggerId);
        }
        finally {
            writeLock.unlock();
        }

        reconcilerThread.interrupt();
    }

    public static Map<String, GregorianCalendar> getPendingNiprSyncDates() {

        Map<String, GregorianCalendar> lNiprSyncDates = new HashMap<String, GregorianCalendar>();

        readLock.lock();
        try {

            GregorianCalendar lToday = (GregorianCalendar) GregorianCalendar.getInstance();
            if(!CalenderUtils.isCalenderDaySame(lToday, lastSuccessfullSync)) {
                System.out.println("Reconciler: Last successful call is not today, adding today's date");
                pendingNiprSyncDates.put(CalenderUtils.getFormattedDate(lToday), lToday);
            }
            else {
                System.out.println("Reconciler: TodaysSuccessfulSync has been done ...");
            }

            for (GregorianCalendar lCal : pendingNiprSyncDates.values()) {
                GregorianCalendar lCopy = (GregorianCalendar)lCal.clone();
                lNiprSyncDates.put(CalenderUtils.getFormattedDate(lCopy), lCopy);
            }
        }
        finally {
            readLock.unlock();
        }

        return lNiprSyncDates;
    }

    public static void addNiprSyncDate(String aInDate) {
        System.out.println("LicenseDB: Adding date " + aInDate + " for Nipr Sync");
        GregorianCalendar lCal = CalenderUtils.getCalenderTimeFromString(aInDate);
        writeLock.lock();
        try {
            pendingNiprSyncDates.put(CalenderUtils.getFormattedDate(lCal), lCal);
        }
        finally {
            writeLock.unlock();
        }
        triggerResync();
    }

    public static void removeNiprSyncDate(String aInDate) {

        System.out.println("LicenseDB: Removing Nipr Sync date " + aInDate);
        GregorianCalendar lCal = CalenderUtils.getCalenderTimeFromString(aInDate);
        Map<String, GregorianCalendar> lDates = new HashMap<String, GregorianCalendar>();
        lDates.put(CalenderUtils.getFormattedDate(lCal), lCal);

        removeNiprSyncDates(lDates);
    }

    public static void removeNiprSyncDates(Map<String, GregorianCalendar> aInSuccessDates) {

        GregorianCalendar lToday = (GregorianCalendar) GregorianCalendar.getInstance();
        writeLock.lock();
        try {
            for(GregorianCalendar lCal : aInSuccessDates.values()) {

                String lKey = CalenderUtils.getFormattedDate(lCal);
                System.out.println("LicenseDB: Removing date " + lKey + " for Nipr Sync");
                pendingNiprSyncDates.remove(lKey);

                if(CalenderUtils.isCalenderDaySame(lToday, lCal)) {
                    System.out.println("LicenseDB: Today date " + CalenderUtils.getFormattedDate(lCal) + " is in the dates to be removed, marking last sync as today");
                    lastSuccessfullSync = lToday;
                }
            }
        }
        finally {
            writeLock.unlock();
        }
    }

    public static Map<String, LicenseInternal> getUnprocessedLicenses() {

        Map<String, LicenseInternal> lCopies = new HashMap<String, LicenseInternal>();

        readLock.lock();
        try {
            for (LicenseInternal lLicense : unprocessedLicenses.values()) {
                LicenseInternal lCopy = lLicense.GetCopy();
                lCopies.put(lCopy.GetKey(), lCopy);
            }
        }
        finally {
            readLock.unlock();
        }

        return lCopies;
    }

    public static List<LicenseInternal> getFailedLicensesByDate(String aInDate) {

        ArrayList<LicenseInternal> lRetVals = new ArrayList<LicenseInternal>();
        readLock.lock();
        try {
            for(LicenseInternal lLicense : unprocessedLicenses.values()) {
                if(CalenderUtils.isNullOrWhiteSpace(aInDate)) {
                    // Add all of them
                    lRetVals.add(lLicense.GetCopy());
                }
                else if(Objects.equals(lLicense.niprUpdateDate, aInDate)) {
                    lRetVals.add(lLicense.GetCopy());
                }
            }
        }
        finally {
            readLock.unlock();
        }


        return lRetVals;
    }

    public static void setUnprocessedLicenses(Map<String, LicenseInternal> aInLicenses) {
        writeLock.lock();
        try {
            unprocessedLicenses = aInLicenses;
        }
        finally {
            writeLock.unlock();
        }
    }
}
