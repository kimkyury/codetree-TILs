import java.util.*;
import java.io.*;

public class Main {

    static class Node{

        int id;
        int color;
        int version;
        int maxD;
        int pId;
        List<Node> child;

        Node(int id, int color, int maxD, int pId, int version){
            this.id = id;
            this.color = color;
            this.maxD = maxD;
            this.pId = pId;

            this.version = version; // 컬러 변경 시점

            child = new ArrayList<>();
        }
    }

    static boolean confirmC(Node node, int depth){
        if (node.pId == -1){
            return true;
        }

        if (node.maxD <= depth){
            return false;
        }
        
        // 상위로 갈수록 더 많은 depth 허용을 유지해야 함

        return confirmC(nodeById.get(node.pId), depth++);
    }

    static Map<Integer, Node> nodeById;
    static Map<Integer, Integer> valueById;
    static List<Node> rootList;
    static StringTokenizer st;

    public static void main(String[] args) throws IOException {

        // System.setIn(new FileInputStream("res/input.txt"));

        nodeById = new HashMap<>();
        rootList = new ArrayList<>();
        valueById = new HashMap<>();

		Scanner sc = new Scanner(System.in);
		int T;
		T=Integer.parseInt(sc.nextLine());

        for(int t =1; t<= T; t++){
            st = new StringTokenizer(sc.nextLine());
            int command = Integer.parseInt(st.nextToken());

            if (command == 100){
                int mId = Integer.parseInt(st.nextToken());
                int pId = Integer.parseInt(st.nextToken());
                int color = Integer.parseInt(st.nextToken());
                int maxD = Integer.parseInt(st.nextToken());
                
                Node node = new Node (mId, color, maxD, pId, t);
                if (pId == -1){
                    rootList.add(node);
                    nodeById.put(mId, node);
                }
                else if (confirmC(nodeById.get(pId),1)){
                    nodeById.get(pId).child.add(node);
                    nodeById.put(mId, node);
                }

            }else if (command == 200){
                int mId = Integer.parseInt(st.nextToken());
                int color = Integer.parseInt(st.nextToken());

                nodeById.get(mId).color = color;
                nodeById.get(mId).version = t; // 중요

            }else if (command == 300){
                int mId = Integer.parseInt(st.nextToken());

                System.out.println(selectColor(mId, nodeById.get(mId).version));

            }else if (command == 400){
                // System.out.print("400:: ");
                int sum =0;
                for(Node node : rootList){
                    sum += (int) getBeauty(node, node.color, node.version)[0];
                    // sum += (int) getBeauty(node, node.color, node.version)[0];
                }

                System.out.println(sum);
            }
        }
    }


    static Object[] getBeauty(Node curr, int color, int version) {
        // root에서부터 내려온 색 정보보다 현재 노드의 색정보가 최신이라면 갱신합니다
        if (version < curr.version) {
            version = curr.version;
            color = curr.color;
        }
        int result = 0;
        ColorCount colorCount = new ColorCount();
        colorCount.cnt[color] = 1;
        for (Node child : curr.child) {
            
            Object[] subResult = getBeauty(child, color, version);
            colorCount = colorCount.add((ColorCount) subResult[1]);
            result += (Integer) subResult[0];
        }
        result += colorCount.score();
        return new Object[] { result, colorCount };
    }

    // 점수 조회 명령을 간편히 구현하기 위한 class입니다
    static class ColorCount {
        int[] cnt = new int[5 + 1];

        // 각 Color의 개수를 합칩니다
        ColorCount add(ColorCount obj) {
            ColorCount res = new ColorCount();
            for (int i = 1; i <= 5; i++) {
                res.cnt[i] = this.cnt[i] + obj.cnt[i];
            }
            return res;
        }

        // 서로다른 색의 개수의 제곱을 반환합니다
        int score() {
            int result = 0;
            for (int i = 1; i <= 5; i++) {
                if (this.cnt[i] > 0) result++;
            }
            return result * result;
        }
    }

    static Object[] selectValue(Node node, int color, int version){

        if (version < node.version){
            version = version;
            color = node.color;
        }

        int [] colorCnt = new int [6];
        colorCnt[color] = 1;
        int total =0;
        for(Node nodeN : node.child){

            // 현재 노드가 가진 최소가치
            Object [] obj = selectValue(nodeN, color, version); // 자식이 해당 color, version을 가졌는지 확인
            colorCnt = add(colorCnt, (int [])obj[1]);
            total += (Integer) obj[0];
        }

        total += getScore(colorCnt);
        return new Object[] {total, colorCnt};
    }

    static int [] add(int [] origin, int [] color){
        int [] newColor = new int [6];
        for(int i =1; i<=5; i++){
            newColor[i] = origin[i] + color[i];
        }
        return newColor;
    }

    static int getScore(int [] color){
        int result = 0;
        for (int i =1; i<=5; i++){
            if (color[i] > 0) result ++;  // 컬러를 가진 자식 수
        }
        return result;
    }



    static int selectColor(int mId, int i){
        // 내 상위에 있는 노드 중에, 가장 i에 가까운 것
        
        Node start = nodeById.get(mId);
        int maxV = i;
        int color = start.color;
        // System.out.println(">i: " + i + ", color: " + color);

        Queue<Node> q = new ArrayDeque<>();
        q.offer(nodeById.get(mId));

        while(!q.isEmpty()){
            
            Node node = q.poll();
            if (maxV < node.version){
                maxV = node.version;
                color = node.color;
                // System.out.println(">>i: " + i + ", color: " + color);

            }

            if (node.pId == -1){
                break;
            }

            q.offer(nodeById.get(node.pId));
        }
        return color;
    }
}