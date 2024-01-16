package hosts;

public class Host {

    public String hostname;
    public HostStatus status;

    public Host(String hostname) {
        this.hostname = hostname;
        this.status=HostStatus.FREE;
    }

}
