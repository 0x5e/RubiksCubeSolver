package com.RubiksCubeSolver;

public class Solve {
    public static char color[][] = new char[6][9];

    static void clean(){
        for(int i=0;i<6;i++)
            for(int j=0;j<9;j++)
                color[i][j]='U';
    }
    
    //面旋转
    static void RotateSurface(int surface,boolean ClockWise){
    	char temp;
    	if(ClockWise){
    		temp=color[surface][0];    		color[surface][0]=color[surface][6];    		color[surface][6]=color[surface][8];    		color[surface][8]=color[surface][2];    		color[surface][2]=temp;
    		temp=color[surface][1];    		color[surface][1]=color[surface][3];    		color[surface][3]=color[surface][7];    		color[surface][7]=color[surface][5];    		color[surface][5]=temp;
    	}else{
    		temp=color[surface][0];    		color[surface][0]=color[surface][2];    		color[surface][2]=color[surface][8];    		color[surface][8]=color[surface][6];    		color[surface][6]=temp;
    		temp=color[surface][1];    		color[surface][1]=color[surface][5];    		color[surface][5]=color[surface][7];    		color[surface][7]=color[surface][3];    		color[surface][3]=temp;
    	}
    }
    
    //机械臂A
    static void RotateCubeA(boolean ClockWise){
    	char temp[] = new char[9];
		for(int i=0;i<6;i++){
			if(i!=2)
				RotateSurface(i,ClockWise);
			else
				RotateSurface(i,!ClockWise);
		}   	
		
    	if(ClockWise){
    		temp=color[1];    		color[1]=color[5];    		color[5]=color[3];    		color[3]=color[4];    		color[4]=temp;
    	}else{
    		temp=color[1];    		color[1]=color[4];    		color[4]=color[3];    		color[3]=color[5];    		color[5]=temp;
    	}

    }
  //机械臂B
    static void RotateCubeB(boolean ClockWise){
    	char temp[] = new char[9];
    	 
		RotateSurface(1,ClockWise);
		RotateSurface(3,!ClockWise);
		RotateSurface(2,ClockWise);
		RotateSurface(2,ClockWise);
		
    	if(ClockWise){
    		RotateSurface(5,ClockWise);
    		RotateSurface(5,ClockWise);
    		temp=color[2];    		color[2]=color[5];    		color[5]=color[0];    		color[0]=color[4];    		color[4]=temp;
    	}else{
    		RotateSurface(4,ClockWise);
    		RotateSurface(4,ClockWise);
    		temp=color[2];    		color[2]=color[4];    		color[4]=color[0];    		color[0]=color[5];    		color[5]=temp;
    	}
    	
    }
    
    //底面旋转
    static void RotateDownA(boolean ClockWise){
    	RotateSurface(2,ClockWise);
    }
    static void RotateDownB(boolean ClockWise){
    	RotateSurface(3,ClockWise);
    }
    final static char ReadQ[] = {'F','R','B','L','D','U'};
    final static int table[]={
    		5*9+7,0*9+1,5*9+5,1*9+1,5*9+1,2*9+1,5*9+3,3*9+1,//UF UR UB UL
    		4*9+1,0*9+7,4*9+5,1*9+7,4*9+7, 2*9+7,4*9+3,3*9+7,//DF DR DB DL
    		0*9+5,1*9+3,0*9+3,3*9+5,2*9+3,1*9+5,2*9+5,3*9+3,//FR FL BR BL
    		5*9+8,0*9+2,1*9+0,//UFR
    		5*9+2,1*9+2,2*9+0,//URB
    		5*9+0,2*9+2,3*9+0,//UBL
    		5*9+6,3*9+2,0*9+0,//ULF
    		4*9+2,1*9+6,0*9+8,//DRF
    		4*9+0,0*9+6,3*9+8,//DFL
    		4*9+6,3*9+6,2*9+8,//DLB
    		4*9+8,2*9+6,1*9+8};//DBR 
    public static String ReadColors(){

    	StringBuilder sInput = new StringBuilder("");  
        for(int i=0;i<48;i++){
        	sInput.append(ColorValue(color[table[i]/9][table[i]%9]));   	
        	if((i<24 && i%2==1)||(i>=24 && i<47 && (i-24)%3==2))
        		sInput.append(" ");
        }
        return sInput.toString();
    }
    static char ColorValue(char c)  
    {  
    	for(int i=0;i<6;i++)
    		if (c == color[i][4]) return ReadQ[i];  
        return '?';  
    }  
    
    public static String ReadColors2(){
    	StringBuffer s = new StringBuffer(54);
		for (int i = 0; i < 54; i++)
			s.insert(i, 'B');
		int a[]={5,1,0,4,3,2};//URFDLB
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 9; j++){
				for(int k=0;k<6;k++)
					if (Solve.color[a[i]][j] == Solve.color[k][4])
						s.setCharAt(9 * i + j, ReadQ[k]);
			}
		}
		return s.toString();
    }
}
