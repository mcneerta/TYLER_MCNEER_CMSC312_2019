public class Instruction{

private String instructionName;
private int numCycles;

  public Instruction(String name, int cycles){
    instructionName = name;
    numCycles = cycles;
  }

  public void setInstructionName(String name){
    instructionName = name;
  }

  public String getInstructionName(){
    return instructionName;
  }

  public void setCycles(int cycles){
    numCycles = cycles;
  }

  public int getCycles(){
    return numCycles;
  }

}
