//package ver14.Model;
//
//public class RndFen {
//    public static void x(){
//        //fengenerator Copyright by Bernd Will 2012
//
//        var chb=new Array(8);
//        for(i=0;i<8;i++){chb[i]=new Array(8);}
//        clearChb();
//
//        var moves= new Object();
//        moves['K']= new Array();
//        moves['K'][0]= Array(0,-1,1);
//        moves['K'][1]= Array(1,-1,1);
//        moves['K'][2]= Array(1,0,1);
//        moves['K'][3]= Array(1,1,1);
//        moves['K'][4]= Array(0,1,1);
//        moves['K'][5]= Array(-1,1,1);
//        moves['K'][6]= Array(-1,0,1);
//        moves['K'][7]= Array(-1,-1,1);
//
//        moves['Q']= new Array();
//        moves['Q'][0]= Array(0,-1,7);
//        moves['Q'][1]= Array(1,-1,7);
//        moves['Q'][2]= Array(1,0,7);
//        moves['Q'][3]= Array(1,1,7);
//        moves['Q'][4]= Array(0,1,7);
//        moves['Q'][5]= Array(-1,1,7);
//        moves['Q'][6]= Array(-1,0,7);
//        moves['Q'][7]= Array(-1,-1,7);
//        moves['q']= moves['Q'];
//
//        moves['R']= new Array();
//        moves['R'][0]= Array(0,-1,7);
//        moves['R'][1]= Array(1,0,7);
//        moves['R'][2]= Array(0,1,7);
//        moves['R'][3]= Array(-1,0,7);
//        moves['r']= moves['R'];
//
//        moves['B']= new Array();
//        moves['B'][0]= Array(1,-1,7);
//        moves['B'][1]= Array(1,1,7);
//        moves['B'][2]= Array(-1,1,7);
//        moves['B'][3]= Array(-1,-1,7);
//        moves['b']= moves['B'];
//
//        moves['N']= new Array();
//        moves['N'][0]= Array(1,-2,1);
//        moves['N'][1]= Array(2,-1,1);
//        moves['N'][2]= Array(2,1,1);
//        moves['N'][3]= Array(1,2,1);
//        moves['N'][4]= Array(-1,2,1);
//        moves['N'][5]= Array(-2,1,1);
//        moves['N'][6]= Array(-2,-1,1);
//        moves['N'][7]= Array(-1,-2,1);
//        moves['n']= moves['N'];
//
//        moves['P']=new Array();
//        moves['P'][0]= Array(-1,-1,1);
//        moves['P'][1]= Array(1,-1,1);
//
//        moves['p']=new Array();
//        moves['p'][0]= Array(-1,1,1);
//        moves['p'][1]= Array(1,1,1);
//
//        var wbf,bbf;
//
//
//
//        function makefen(){
////strFig='rnbqbnrppppppppPPPPPPPPRNBQBNR';// figure pool
//            strFig='';
//            n=parseInt(document.getElementById('fQ').value);for(i=1;i<=n;i++){strFig+='Q';}
//            n=parseInt(document.getElementById('fR').value);for(i=1;i<=n;i++){strFig+='R';}
//            n=parseInt(document.getElementById('fB').value);for(i=1;i<=n;i++){strFig+='B';}
//            n=parseInt(document.getElementById('fN').value);for(i=1;i<=n;i++){strFig+='N';}
//            n=parseInt(document.getElementById('fP').value);for(i=1;i<=n;i++){strFig+='P';}
//            n=parseInt(document.getElementById('fq').value);for(i=1;i<=n;i++){strFig+='q';}
//            n=parseInt(document.getElementById('fr').value);for(i=1;i<=n;i++){strFig+='r';}
//            n=parseInt(document.getElementById('fb').value);for(i=1;i<=n;i++){strFig+='b';}
//            n=parseInt(document.getElementById('fn').value);for(i=1;i<=n;i++){strFig+='n';}
//            n=parseInt(document.getElementById('fp').value);for(i=1;i<=n;i++){strFig+='p';}
//
////alert(strFig);
//            pl=strFig.length;
//
//            clearChb();
//
//            wbf='';bbf='';
//
//            nfig=document.getElementById('sfig').value;
//            if(nfig>pl+2){nfig=pl+2;}
//
////1. set black King
//            f=getEmptyField();
//            chb[f[0]][f[1]]='k';
//
////2. set white King
//            do{f=getEmptyField();}while(!allowedPos(f[0],f[1],'K'))
//            chb[f[0]][f[1]]='K';
//            showField();
//
////3. other figures
//            for(n=2;n<nfig;n++){
//                //get random figure from pool
//                do{pos=parseInt(pl*Math.random());}while(strFig.substr(pos,1)=='_');
//                cfig=strFig.substr(pos,1);
//                strFig=strFig.substr(0,pos)+'_'+strFig.substr(pos+1,pl-1-pos);
//
//                // set figure
//                do{f=getEmptyField();/*alert((n+1)+": Try to set "+cfig+" on "+f);*/}while(!allowedPos(f[0],f[1],cfig))
//                chb[f[0]][f[1]]=cfig;
//                showField();
//            }
//
////make fen
//            cFen='';
//            z=0;
//
//            for(y=0;y<8;y++){
//                for(x=0;x<8;x++){
//                    if(x==0){
//                        if(z>0){cFen+=z;}
//                        if(x>0 || y>0){cFen+='/';}
//                        z=0;
//                    }
//                    if(chb[x][y]==''){z++;}
//                    else{
//                        if(z>0){cFen+=z;}
//                        cFen+=chb[x][y];
//                        z=0;
//                    }
//                }
//            }
//            if(z>0){cFen+=z;}
//            cFen+=' w - - 0 1';
//
//            document.getElementById('fen').value=cFen;
//        }
//
////-------------------------------------------------------------------------------
//
//        function showField(){
//            for(y=0;y<8;y++){for(x=0;x<8;x++){
//                fig=chb[x][y];
//                if(fig!=''){
//                    if(fig=='K' || fig=='Q' || fig=='R' || fig=='B' || fig=='N' || fig=='P'){pre='w';}
//                    if(fig=='k' || fig=='q' || fig=='r' || fig=='b' || fig=='n' || fig=='p'){pre='b';}
//                    fig=fig.toLowerCase();
//                    document.getElementById('f-'+x+'-'+y).innerHTML='<img src="pcs/'+pre+fig+'.png" />';
//                }
//                else{document.getElementById('f-'+x+'-'+y).innerHTML='';}//<span style="font-size:11px;">'+x+'-'+y+'</span>';}
//            }}
//        }
//
//        function allowedPos(x,y,fig){
//
//            if(fig=='K' || fig=='Q' || fig=='R' || fig=='B' || fig=='N' || fig=='P'){op_king='k';}
//            if(fig=='k' || fig=='q' || fig=='r' || fig=='b' || fig=='n' || fig=='p'){op_king='K';}
//            //no king in chess!
//
//            for(d=0;d<moves[fig].length;d++){
//                dx=moves[fig][d][0];
//                dy=moves[fig][d][1];
//                l=moves[fig][d][2];
//                for(s=1;s<=l;s++){//alert(s);
//                    px=x+s*dx;
//                    py=y+s*dy;
//                    if(px>=0 && py>=0 && px<8 && py<8){
//                        if(chb[px][py]!=''){
//                            if(chb[px][py]==op_king){/*alert(op_king+' von '+fig+' auf '+x+'-'+y+' bedroht');*/return false;}
//                            else{s=l+1}
//                        }
//                    }else{s=l+1;}
//                }
//            }
//
//            //P/p not on baselines
//            if(fig=='P' || fig=='p'){if(y<1 || y>6){/*alert(fig+' draussen');*/return false;}}
//
//            //B/b on black and white fields
//            fc=((x+y)%2==0)?'w':'b';
//
//            if(fig=='B'){
//                if(wbf==fc){/*alert(fig+': '+fc+' schon besetzt');*/return false;}
//                else{if(wbf==''){wbf=fc;}}
//            }
//            if(fig=='b'){
//                if(bbf==fc){/*alert(fig+': '+fc+' schon besetzt');*/return false;}
//                else{if(bbf==''){bbf=fc;}}
//            }
//
//            return true;
//        }
//
//        function clearChb(){
//            for(y=0;y<8;y++){for(x=0;x<8;x++){chb[x][y]='';}}
//        }
//
//        function getEmptyField(){
//            do{x=parseInt(8*Math.random());y=parseInt(8*Math.random())}while(chb[x][y]!='');
//            f=new Array(x,y);
//            return f;
//        }
//
//        function chgSel(flag){
//                //alert(location.search);
//
//                n=2;
//        n+=parseInt(document.getElementById('fQ').value);
//        n+=parseInt(document.getElementById('fR').value);
//        n+=parseInt(document.getElementById('fB').value);
//        n+=parseInt(document.getElementById('fN').value);
//        n+=parseInt(document.getElementById('fP').value);
//        n+=parseInt(document.getElementById('fq').value);
//        n+=parseInt(document.getElementById('fr').value);
//        n+=parseInt(document.getElementById('fb').value);
//        n+=parseInt(document.getElementById('fn').value);
//        n+=parseInt(document.getElementById('fp').value);
//        //alert(n);
//        obj=document.getElementById('sfig');
//
//
//
//        //if(!flag){
//        for(i=2;i<=32;i++){
//            if(i>n){obj.options[i-2].disabled=true;}
//            else{obj.options[i-2].disabled=false;}
//        }
//        if(obj.value>n){obj.value=n;}
//        document.getElementById('npool').innerHTML=n;
//        //}
//
//
//        cq=new Array();
//        cq[0]=document.getElementById('fQ').value;
//        cq[1]=document.getElementById('fR').value;
//        cq[2]=document.getElementById('fB').value;
//        cq[3]=document.getElementById('fN').value;
//        cq[4]=document.getElementById('fP').value;
//        cq[5]=document.getElementById('fq').value;
//        cq[6]=document.getElementById('fr').value;
//        cq[7]=document.getElementById('fb').value;
//        cq[8]=document.getElementById('fn').value;
//        cq[9]=document.getElementById('fp').value;
//        cq[10]=document.getElementById('sfig').value;
//
//
//
//        if(!flag){location.href='fengenerator.html?'+cq.join(',')};
//
//}
//
//        function init(){
//
//            document.getElementById('sfig').options.selectedIndex=14;
//            if(location.search!=''){
//                cq=location.search.replace(/\?/,'').split(',');
//                document.getElementById('fQ').value=cq[0];
//                document.getElementById('fR').value=cq[1];
//                document.getElementById('fB').value=cq[2];
//                document.getElementById('fN').value=cq[3];
//                document.getElementById('fP').value=cq[4];
//                document.getElementById('fq').value=cq[5];
//                document.getElementById('fr').value=cq[6];
//                document.getElementById('fb').value=cq[7];
//                document.getElementById('fn').value=cq[8];
//                document.getElementById('fp').value=cq[9];
//                document.getElementById('sfig').value=cq[10];
//            }
//            chgSel(true);
//        }
//    }
//}
