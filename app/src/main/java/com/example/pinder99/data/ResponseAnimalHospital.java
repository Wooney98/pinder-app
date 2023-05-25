package com.example.pinder99.data;

import com.example.pinder99.data.HospitalInfoDto;

import java.util.List;

public class ResponseAnimalHospital {
    private int page;
    private int perPage;
    private int totalCount;
    private int currentCount;
    private int matchCount;
    private List<HospitalInfoDto> data;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPerPage() { return perPage; }
    public void setPerPage(int perPage) { this.perPage = perPage; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getCurrentCount() { return currentCount; }
    public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }

    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }

    public List<HospitalInfoDto> getData() { return data; }
    public void setData(List<HospitalInfoDto> data) { this.data = data; }
}
