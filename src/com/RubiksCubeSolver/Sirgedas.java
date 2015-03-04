package com.RubiksCubeSolver;
public class Sirgedas {
	static String data = "2#6'&78)5+1/AT[NJ_PERLQO@IAHPNSMBJCKLRMSDHEJNPOQFKGIQLSNF@DBROPMAGCEMPOACSRQDF";
	static char 
		move[]="FBRLUD".toCharArray(),
		inva[]=new char [48], b[]=new char [48], 
		cur_phase, search_mode, history_idx, 
		history_mov[]=new char [48], history_rpt[]=new char [48],
		depth_to_go[]=new char[5 << 20], 
		hash_table[][] =new char[48][6912],
		pos[]=new char[48],twi[]=new char[48];

	static void rot(char cur_phase ){
		if ( cur_phase < 4)
			for (int i = -1; ++ i < 4 ;) {
				twi[64^data.charAt(20+cur_phase*8+i )] = (char) ((twi[64^data.charAt(20+cur_phase*8+i )] + 2 - i%2) % 3);
				twi[64^data.charAt(20+cur_phase*8+i +4 )] ^= (cur_phase < 2)?1:0;
			}

		for (int i = -1; ++ i < 7 ;){
			char temp;
			temp = twi[64^data.charAt(20+cur_phase*8+i +(i!=3?1:0) )];
			twi[64^data.charAt(20+cur_phase*8+i +(i!=3?1:0) )] = twi[64^data.charAt(20+cur_phase*8+i )];
			twi[64^data.charAt(20+cur_phase*8+i )] = temp;
			
			temp = pos[64^data.charAt(20+cur_phase*8+i +(i!=3?1:0) )];
			pos[64^data.charAt(20+cur_phase*8+i +(i!=3?1:0) )] = pos[64^data.charAt(20+cur_phase*8+i )];
			pos[64^data.charAt(20+cur_phase*8+i )] = temp;
		}
	}
	static int hashf( ){	
		int ret = 0;	
		switch(cur_phase){
			case 0:
				for (int i = -1; ++ i < 11 ;) 
					ret += ret + twi [i ];	
				return ret;
			case 1:
				for (int i = -1; ++ i < 7 ;) 
					ret = ret*3 + twi [i +12];	
				for (int i = -1; ++ i < 11 ;) 
					ret += ret + (pos [i ] > 7 ?1:0);		
				return ret-7;
			case 2:
				for (int i = -1; ++ i < 8 ;){ 
					if ( pos [i +12]<16)
						inva[pos [i +12]&3] = (char) ret++;
					else
						b [i -ret] = (char) (pos [i +12]&3);	
				}
				for (int i = -1; ++ i < 7 ;) 
					ret += ret + (pos [i ] > 3 ?1:0 );
				for (int i = -1; ++ i < 7 ;)
					ret += ret + (pos [i +12] > 15 ?1:0);		
				return ret*54 + (inva[b[0 ]] ^inva[b[ 1 ]] )*2 + (((inva[b[0 ]] ^inva[b[ 2 ]] ) > (inva[b[0 ]] ^inva[b[ 3 ]] ))?1:0) - 3587708;
		}
		for (int i = -1; ++ i < 5 ;) {
			ret *= 24;
			int cur_phase;
			for (cur_phase = -1; ++ cur_phase < 4 ;)
				for (int k = -1; ++ k < cur_phase ;)
					if ( pos [i *4+cur_phase] < pos [i *4+k]) 
						ret += cur_phase << cur_phase/3;
		}
		return ret/2;
	}

	static int do_search(int dpt ){
		int h = hashf(), q = cur_phase/2*19+8 << 7;
		if ( (dpt < hash_table[cur_phase  ][h%q] | dpt < hash_table[cur_phase+4][h/q]) ^ search_mode !=0){
			if ( search_mode!=0) {
				if ( dpt <= depth_to_go[h]) return h==0?1:0;	
				else depth_to_go[h] = (char) dpt;
			}
				hash_table[cur_phase  ][h%q] <<= dpt;
				hash_table[cur_phase+4][h/q] <<= dpt;

				for (int k = -1; ++ k < 6 ;) 
					for (int i = -1; ++ i < 4 ;) {
						rot((char) k );
						if ( k < cur_phase*2 & i != 1 || i > 2) continue;	
						history_mov[history_idx] = (char) k;		
						history_rpt[history_idx++] = (char) i;
						if ( do_search(dpt-search_mode*2+1)!=0) return 1;
						history_idx--;
					}
		}
		return 0;
	}

	public static String GetResult(String sInput){

        String[] argv = sInput.split(" ");
        String sOutput = "";
        if (argv.length != 20)	return "error";
        
		for(int i=0;i<48;i++)
			for(int j=0;j<6912;j++)
				hash_table[i][j]=6;
		for (int i = -1; ++ i < 20 ;) 
			pos [i ] = (char) i;
		for (cur_phase = (char) -1; ++ cur_phase < 4 ;) 
			do_search(0);	
		for (int i = -1; ++ i < 20 ;){
			String s = argv [i] + "!";
			pos [i] = (char) data.indexOf(s.charAt(0) ^ s.charAt(1) ^ s.charAt(2) );
			int x = Math.min(s.indexOf(85), s.indexOf(68) );
			twi [i] = (char) ((~x!=0) ? x : (s.charAt(0)>70?1:0));
		}
		for (int i = -1; ++ i < 5 ;) {
			char temp;
			temp = twi[64^data.charAt(20+cur_phase*8+i +16)];
			twi[64^data.charAt(20+cur_phase*8+i +16)] = twi[64^data.charAt(20+cur_phase*8+i +21 )];
			twi[64^data.charAt(20+cur_phase*8+i +21 )] =temp;
			
			temp = pos[64^data.charAt(20+cur_phase*8+i +16)];
			pos[64^data.charAt(20+cur_phase*8+i +16)] = pos[64^data.charAt(20+cur_phase*8+i +21 )];
			pos[64^data.charAt(20+cur_phase*8+i +21 )] =temp;
		}
		search_mode = 1;
		for (cur_phase = (char) -1; ++ cur_phase < 4 ;) 
			for (int i = -1; ++ i < 20 ;) 
				if (do_search(i)!=0) break;
		for (int k = -1; ++ k < history_idx ;){
			sOutput += move[history_mov[k]];
			sOutput += history_rpt[k]+1;
			sOutput += " ";
		}

		return sOutput;
	}

}
