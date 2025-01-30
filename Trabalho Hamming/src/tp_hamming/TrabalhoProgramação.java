package tp_hamming;

import java.util.Random;
import java.util.Scanner;

public class TrabalhoProgramação {

    private static boolean bitsHemming[] = new boolean[12];                     //Variável criada para armazenar os 8 bits originais + os 4 adicionais
    private static String mensagemFinal = "";                                   //Variável para armazenar a mensagem final que aparecerá para o usuário

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.println("Informe a mensagem");
        String mens = entrada.nextLine();                                       //String que armazenará a mensagem inicial enviada

        transmissorEnviaDado(mens);                                     //Chamando função que transmitirá a mensagem

        System.out.println("A mensagem recebida no receptor foi: " + mensagemFinal);

    }

    public static void transmissorEnviaDado(String mensagem) {                  
        //cada caracter é representado por um array de bits e enviado para o receptor
        for (int i = 0; i < mensagem.length(); i++) {
            boolean bits[] = streamCaracter(mensagem.charAt(i));

            /*-------AQUI você deve adicionar os bits de Hemming para contornar os problemas de ruidos
                        você pode modificar o método anterior também*/
            transmissorDadoBitsHemming(bits);                                   //Chamando função que adicionará os bits originais + os bits de Hemming

            //add ruidos na mensagem a ser enviada para o receptor
            geradorRuido();                                                     //Chamando função que irá simular o ruído no processo de envio da mensagem

            //enviando a mensagem "pela rede" para o receptor*/
            receberDadoBits(bitsHemming);                                  //Chamando função que irá simular a recepção dos dados
        }
    }

//não modifique (seu objetivo é corrigir esse erro gerado no receptor)
    public static void geradorRuido() {
        Random geradorAleatorio = new Random();

        //pode gerar um erro ou não..
        if (geradorAleatorio.nextInt(5) > 1) {
            int indice = geradorAleatorio.nextInt(8);
            bitsHemming[indice] = !bitsHemming[indice];
        }
    }

    public static void receberDadoBits(boolean bits[]) {
        //aqui você deve trocar o médodo decofificarDado para receptorDecoficarDadoHemming (implemente!!)

        receptorDecoficarDadoHemming(bits);                                     //Função chamando o receptor, que irá decodificar os dados
        // receptorDecodificarDado(bitsHemming);
    }

    //convertendo um símbolo para "vetor" de boolean (bits)
    private static boolean[] streamCaracter(char simbolo) {

        //cada símbolo da tabela ASCII é representado com 8 bits
        boolean bits[] = new boolean[8];

        //convertendo um char para int (encontramos o valor do mesmo na tabela ASCII)
        int valorSimbolo = (int) simbolo;
        int indice = 7;

        //convertendo cada "bits" do valor da tabela ASCII
        while (valorSimbolo >= 2) {
            int resto = valorSimbolo % 2;
            valorSimbolo /= 2;
            bits[indice] = (resto == 1);
            indice--;
        }
        bits[indice] = (valorSimbolo == 1);

        return bits;
    }

    private static void transmissorDadoBitsHemming(boolean bits[]) {

        /*sua implementação aqui!!!
        modifique o que precisar neste método
        é esperado que você faça o retorne de um array de mais posições que o original
         */
        boolean x1 = bits[0] ^ bits[1] ^ bits[3] ^ bits[4] ^ bits[6];           //Cálculo dos 4 bits de Hemming a partir do XOR
        boolean x2 = bits[0] ^ bits[2] ^ bits[3] ^ bits[5] ^ bits[6];
        boolean x4 = bits[1] ^ bits[2] ^ bits[3] ^ bits[7];
        boolean x8 = bits[4] ^ bits[5] ^ bits[6] ^ bits[7];

        int contadorexterno = 0;                                                //Contador que servirá como índice para a variável bits
                                                                                //(dos bits originais) para adicioná-los na varíável de 12 posições
        for (int i = 0; i < 12; i++) {                                          //Laço para adicionar todos os bits dentro da variável bitsHemming
            if (i == 0 || i == 1 || i == 3 || i == 7) {                         //Condição para verificar se o índice representa a posição dos bits 
                if (i == 0) {                                                   //adicionais
                    bitsHemming[0] = x1;
                }
                if (i == 1) {
                    bitsHemming[1] = x2;
                }
                if (i == 3) {
                    bitsHemming[3] = x4;
                }
                if (i == 7) {
                    bitsHemming[7] = x8;
                }
            } else {                                                            //Condição para adicionar os 8 bits originais em suas devidas posições
                bitsHemming[i] = bits[contadorexterno];                         //dentro das 12
                contadorexterno++;
            }
        }
    }

    private static void receptorDecodificarDado(boolean bits[]) {               //Trasnformando os bits em caractér de acordo com a tabela Ascii     
        int codigoAscii = 0;                                                    //e os adicionando na variável de mensagemFinal
        int expoente = bits.length - 1;

        //converntendo os "bits" para valor inteiro para então encontrar o valor tabela ASCII
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                codigoAscii += Math.pow(2, expoente);
            }
            expoente--;
        }

        //concatenando cada simbolo na mensagem original
        mensagemFinal += (char) codigoAscii;
    }

    private static void receptorDecoficarDadoHemming(boolean bits[]) {
        //implemente a decodificação Hemming aqui e encontre os 
        //erros e faça as devidas correções para ter a imagem correta

        boolean k1 = bits[0] ^ bits[2] ^ bits[4] ^ bits[6] ^ bits[8] ^ bits[10];//Fazendo o processo inverso e calculando os 4 bits verificadores
        boolean k2 = bits[1] ^ bits[2] ^ bits[5] ^ bits[6] ^ bits[9] ^ bits[10];
        boolean k3 = bits[3] ^ bits[4] ^ bits[5] ^ bits[6] ^ bits[11];
        boolean k4 = bits[7] ^ bits[8] ^ bits[9] ^ bits[10] ^ bits[11];

        if (k1 == true || k2 == true || k3 == true || k4 == true) {             //Verificando se algum bit tem o valor 1, o que representa que há
            int soma = 0;                                                       //erro na sequência dos dados

            if (k1 == true) {
                soma = soma + 1;                                                //Realizando soma das potências de 2 para converter base binária em 
            }                                                                   //decimal e ver onde está o erro
            if (k2 == true) {
                soma = soma + 2;
            }
            if (k3 == true) {
                soma = soma + 4;
            }
            if (k4 == true) {
                soma = soma + 8;
            }

            bitsHemming[soma - 1] = !bitsHemming[soma - 1];                     //Invertendo o valor do bit na devida posição de erro
        }

        boolean bitsCorrigidos[] = new boolean[8];                              //Adicionando os 8 bits já corrigidos para transformá-los em
        bitsCorrigidos[0] = bitsHemming[2];                                     //caractér novamente
        bitsCorrigidos[1] = bitsHemming[4];
        bitsCorrigidos[2] = bitsHemming[5];
        bitsCorrigidos[3] = bitsHemming[6];
        bitsCorrigidos[4] = bitsHemming[8];
        bitsCorrigidos[5] = bitsHemming[9];
        bitsCorrigidos[6] = bitsHemming[10];
        bitsCorrigidos[7] = bitsHemming[11];

        receptorDecodificarDado(bitsCorrigidos);                           //Chamando função que irá transformar os bits em caractér novamente         
    }

    //recebe os dados do transmissor
}
