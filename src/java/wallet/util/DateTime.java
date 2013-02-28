package wallet.util;

public final class DateTime {

	private DateTime() {
        throw new UnsupportedOperationException();
    }
        
    public static final TimeDistance timeDistance(final long t0, final long t) {
        return timeDistance(t0, t, null);
    }
    
    public static final TimeDistance timeDistance(final long t0, final long t, TimeDistance timeDistance) {
        long ms = Math.abs(t - t0);
        if (ms < 0) {
            throw new UnsupportedOperationException("Timestamps diff is Long.MinValue - should never happen");
        }
        if (ms != 0) {
            ms /= 1000000;
        }
        if (timeDistance != null) {
            timeDistance.sec100 = 0;
            timeDistance.sec = 0;
            timeDistance.min = 0;
            timeDistance.hr = 0;
            timeDistance.day = 0;
        } else {            
            timeDistance = new TimeDistance();            
        }
        if (ms < 5) {
            return timeDistance;
        }
        if ((ms % 10) < 5) {
            ms /= 10;
        } else {
            ms /= 10;
            ++ms;
        }
        timeDistance.sec100 = (int) (ms % 100);
        ms /= 100;
        if (ms == 0) {
            return timeDistance;
        }        
        timeDistance.sec = (int) (ms % 60);
        ms /= 60;
        if (ms == 0) {
            return timeDistance;
        }        
        timeDistance.min = (int) (ms % 60);
        ms /= 60;
        if (ms == 0) {
            return timeDistance;
        }        
        timeDistance.hr = (int) (ms % 24);
        ms /= 24;
        if (ms == 0) {
            return timeDistance;
        }        
        timeDistance.day = ms;  
        return timeDistance;
    }
    
    public static class TimeDistance {
        
        private static final String[] numbers = new String[100];
        static {
            for (int i = 0; i < numbers.length; ++i) {
                numbers[i] = (i < 10 ? "0" : "") + String.valueOf(i); 
            }
        }
        
        public int sec100;
        public int sec;
        public int min;
        public int hr;
        public long day;  
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean appended = false;
            if (day > 0) {                
                sb.append(day < numbers.length ? numbers[(int) day] : day).append('d').append(':');
                appended = true;                
            }
            if (hr > 0 || appended) {                
                sb.append(hr < numbers.length ? numbers[hr] : hr).append(':');
                appended = true;
            }
            if (min > 0 || appended) {                
                sb.append(min < numbers.length ? numbers[min] : min).append(':');
                appended = true;
            }
            return sb.append(sec < numbers.length ? numbers[sec] : sec).append('s').append(':')
                .append(sec100 < numbers.length ? numbers[sec100] : sec100).toString();            
        }                
        
    }

}
