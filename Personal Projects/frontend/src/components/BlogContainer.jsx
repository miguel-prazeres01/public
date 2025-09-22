import { Box, Button, Container, Flex, Text, useColorModeValue, Image, Stack, Heading, Center, AbsoluteCenter, Tag} from "@chakra-ui/react";

const BlogContainer = ({showId}) => {
    const imageName = "/Cartaz" + showId + ".jpg";

    return (
            <Container minW={'4xl'} maxW={'4xl'} py={4}>
                <Stack my={4} spacing={6} direction='row' align='center' justify={'center'}>
                    <Box p={4} shadow='md' borderWidth='1px'>
                        <Text align={'justify'} fontFamily={"Verdana, sans-serif"} fontSize='xl' >
                            VERDADE E CONSEQUÊNCIA <br/> <br/>
                            📍Minde | 📅 {showId == 1 ? "20 de junho" : "15 de junho"} <br/> <br/>
                            O verão chegou ao fim e, com ele, a promessa de um novo ano letivo. Responsável por ter divulgado segredos, exposto verdades inconvenientes e virado vidas do avesso no ano anterior, a irreverente e viral app Má Lingua, também está de volta. O que começa como um jogo provocante entre o criador da app e os colegas da escola ganha contornos dramáticos quando o pior acontece. Nesta escola onde o drama ofusca a inocência, apenas uma coisa é certa: todos temos segredos! <br/>
                            🎟 Bilhetes à venda nos locais habituais (A Reliquia e www.cpminde.pt) <br/> <br/>
                            📍Cine-Teatro Rogério Venâncio

                        </Text>
                    </Box>
                    <Box p={4} minW={"sm"} shadow='md' borderWidth='1px'>
                        <Center>
                            <Image p={4} src={imageName} fit={'fill'} alt='casa do povo'/>
                        </Center>
                    </Box>
                </Stack>
                <Box p={5} shadow='md' borderWidth='1px'>
                    <Heading align={'center'} fontFamily={"Verdana, sans-serif"} fontSize='xl'>Selecione os seus lugares</Heading>
                    <Center>
                        <Box mt={4}>
                            <Tag size={'lg'} fontSize={'xl'} mx={2} bg='purple.100'>Livre</Tag>
                            <Tag size={'lg'} fontSize={'xl'} mx={2} bg='blue.100'>Selecionado</Tag>
                            <Tag size={'lg'} fontSize={'xl'} mx={2} bg='red.600' opacity={0.5}>Ocupado</Tag>
                        </Box>
                    </Center>
                </Box>
            </Container>
    );
};
export default BlogContainer;