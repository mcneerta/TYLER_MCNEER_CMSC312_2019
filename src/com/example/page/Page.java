package src.com.example.page;

import src.com.example.instruction.Instruction;

public class Page {
    private Instruction instruction;

    public Page(Instruction instruction){
        this.instruction = instruction;
    }

    public void setInstruction(Instruction instruction){
        this.instruction = instruction;
    }

    public Instruction getInstruction(){
        return instruction;
    }

}
