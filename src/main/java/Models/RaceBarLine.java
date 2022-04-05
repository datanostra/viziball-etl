package Models;

import java.util.Arrays;
import java.util.List;

public class RaceBarLine {

  public String name;
  public Float value;
  public String year;
  public Float lastValue;
  public int rank;
  public int tid;

  public RaceBarLine(String name, Float value, String year, Float lastValue, int rank, int tid){
    this.name = name;
    this.value = value;
    this.year = year;
    this.lastValue = lastValue;
    this.rank = rank;
    this.tid = tid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Float getValue() {
    return value;
  }

  public void setValue(Float value) {
    this.value = value;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Float getLastValue() {
    return lastValue;
  }

  public void setLastValue(Float lastValue) {
    this.lastValue = lastValue;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }


  @Override
  public String toString(){
    return String.join(",", Arrays.asList(this.name, this.value.toString(), this.year, this.lastValue.toString(), this.rank+"", this.tid+""));
  }
}
