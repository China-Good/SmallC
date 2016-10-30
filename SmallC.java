import java.io.*;

/**
 * Created by hyx on 16-10-26.
 */
public class SmallC {


    private static final int al = 10;//标识符的最大长度
    private static final int nmax = 14;//数字的最大位数
    private static final int norw = 8;//保留字数量

    private enum symbol {
        nul,         ident,     number,     plus,      minus,
        times,       slash,     oddsym,     eql,       neq,
        lss,         leq,       gtr,        geq,       lparen,
        rparen,      comma,     semicolon,  period,    becomes,
        endsym,      ifsym,     thensym,    elsesym,   repeatsym,
        untilsym,    readsym,   writesym
    }

    private static String url;//输入文件路径
    private static int cc;//当前读取字符在当前行中的位置
    private static int ll;//当前读取字符所在行的长度
    private static char ch;//当前读取的字符,getch使用
    private static String id;//当前的identifier
    private static String[] word = new String[norw];//保留字数组
    private static String a;//存放临时符号
    private static symbol  sym;//存放现在的符号
    private static symbol[] wsym = new symbol[norw];//保留字符号数组
    private static symbol[] ssym = new symbol[256];//单字符符号数组
    private static int num;//存放当前读取的数字

    private static String line;//当前读取行的内容
    private static FileReader fr;
    private static BufferedReader br;
    private static FileWriter fw;
    private static BufferedWriter bw;

    public static void main(String[] args) throws IOException{
        url = args[0];

        init();
        getsym();



        fr.close();
        fw.close();
    }
    public static void init() throws IOException{
        fr = new FileReader(url);//创建FileReader实例
        br = new BufferedReader((fr));//创建BufferedReader实例
        File output = new File("output.txt");
        if(!output.exists())
            output.createNewFile();
        fw = new FileWriter(output.getAbsoluteFile());
        bw = new BufferedWriter(fw);
        cc = 0;//初始化
        ll = 0;//初始化
        ch = ' ';//初始化

        word[0] = new String("else");
        word[1] = new String("end");
        word[2] = new String("if");
        word[3] = new String("read");
        word[4] = new String("repeat");
        word[5] = new String("then");
        word[6] = new String("until");
        word[7] = new String("write");

        wsym[0] = symbol.elsesym;
        wsym[1] = symbol.endsym;
        wsym[2] = symbol.ifsym;
        wsym[3] = symbol.readsym;
        wsym[4] = symbol.repeatsym;
        wsym[5] = symbol.thensym;
        wsym[6] = symbol.untilsym;
        wsym[7] = symbol.writesym;

        for(int i = 0; i < 256; i ++)
            ssym[i] = symbol.nul;

        ssym['+'] = symbol.plus;
        ssym['-'] = symbol.minus;
        ssym['*'] = symbol.times;
        ssym['/'] = symbol.slash;
        ssym['('] = symbol.lparen;
        ssym[')'] = symbol.rparen;
        ssym['='] = symbol.eql;
        ssym[','] = symbol.comma;
        ssym['.'] = symbol.period;
        ssym['#'] = symbol.neq;
        ssym[';'] = symbol.semicolon;




    }
    public static void error(){
        System.out.println("error");
    }

    public static void getsym() throws IOException{
        int i,j,k;
        a = new String();//初始化当前的临时变量
        while(ch == ' ' || ch == '\t' || ch == '\n')//过滤空格,制表符和换行符
            getch();
        //标识符或保留字
        if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')){
            k = 0;
            do{
                if(k < al){
                    a += ch;
                    k ++;
                }
            }
            while((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'));
            id = new String(a);
            i = 0;//下限
            j = norw - 1;//上限
            //使用二分法寻找是否该标识符是保留字
            do{
                k = (i + j) / 2;
                if(id.compareTo(word[k]) < 0)
                    j = k - 1;
                if(id.compareTo(word[k]) > 0)
                    i = k + 1;

            }while(i <= j);
            if (i-1 > j) // 当前的单词是保留字
            {
                sym = wsym[k];
            }
            else // 当前的单词是标识符
            {
                sym = symbol.ident;
            }

        }
        else{//当前符号不是保留字或标识符
            //数字
            if(ch >= '0' && ch <= '9'){
                num = 0;
                sym = symbol.number;
                k = 0;
                do{
                    num = 10 * num + ch - '0';
                    k ++;
                    getch();
                }while(ch >= '0' && ch <= '9');

                k-= 1;//获取读进来数字的长度
                if(k > nmax)
                    error();
            }
            else
            {
                if (ch == ':')		/* 检测赋值符号 */
                {
                    getch();
                    if (ch == '=')
                    {
                        sym = symbol.becomes;
                        getch();
                    }
                    else
                    {
                        sym = symbol.nul;	/* 不能识别的符号 */
                    }
                }
                else
                {
                    if (ch == '<')		/* 检测小于或小于等于符号 */
                    {
                        getch();
                        if (ch == '=')
                        {
                            sym = symbol.leq;
                            getch();
                        }
                        else
                        {
                            sym = symbol.lss;
                        }
                    }
                    else
                    {
                        if (ch == '>')		/* 检测大于或大于等于符号 */
                        {
                            getch();
                            if (ch == '=')
                            {
                                sym = symbol.geq;
                                getch();
                            }
                            else
                            {
                                sym = symbol.gtr;
                            }
                        }
                        else
                        {
                            sym = ssym[ch];		/* 当符号不满足上述条件时，全部按照单字符符号处理 */
                            if (sym != symbol.period)
                            {
                                getch();
                            }

                        }
                    }
                }
            }

        }





    }

    public static void getch() throws IOException{
        if (cc == ll){//缓冲区中的内容已经被依次读取完
            if((line = br.readLine()) == null) {//读取下面一行
                System.out.println("Program incomplete");//如果已经读取完所有代码,则说明出错了
                fw.close();
                fr.close();
                System.exit(1);
            }
            System.out.println(line);
            //读取新的一行代码,并将当前的坐标cc置为0,当前行长度ll置为line的长度
            cc = 0;
            ll = line.length();
            bw.write(line);
            bw.newLine();
            bw.flush();

        }

        //在该行内,逐个读取字符
        ch = line.charAt(cc);
        cc++;
    }
}
