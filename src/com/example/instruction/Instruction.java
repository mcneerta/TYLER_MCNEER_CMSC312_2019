package src.com.example.instruction;

/*
** This is the class to create instruction objects
 */
public class Instruction{

    /*
    ** instructionName is the name of the instruction
    ** numCycles is the number of cycles it takes to complete the instruction
     */
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
