import {Button, Text, Card, CardBody, CardFooter, Stack, Divider, Heading, ButtonGroup, Image, Flex, Spinner, Center, Box} from "@chakra-ui/react";
import {useState, useEffect} from "react";
import {SECRET} from "../App";
import { signPayload } from "../App";
import { BASE_URL } from "../App";
import ShowCard from "../components/ShowCard";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

const Home =  () => {
    const [isLoading, setIsLoading] = useState(true);
    const [shows, setShows] = useState([]);
    
    useEffect(() => {
        const getShows = async () => {
            try {
                const payload = "";
                const signature = signPayload(payload, SECRET);
                const res = await fetch(BASE_URL + "/shows", {
                    method: "GET",
                    headers: {
                    "Content-Type": "application/json",
                    "X-Signature": signature,
                },
                });
                const data = await res.json();

                if (!res.ok) {
                    throw new Error(data.error);
                }
                setShows(data["shows"]);
            } catch (error) {
                console.error(error);
            } finally {
                setIsLoading(false);
            }
        };
        getShows();
    }, [setShows]);
   

    return (
        <>
        <Navbar />
        <Center>
            <Flex maxW={'4xl'} p={4} mb={4} shadow='md' borderWidth='1px'>
                <Text align={'center'} fontFamily={"Verdana, sans-serif"} fontSize='lg' >
                    O valor dos bilhetes reverte inteiramente para a reabilitação do Cine-Teatro Rogério Venâncio. <br/>
                    A Casa do Povo de Minde e o Boca de Cena agradecem profundamente o seu apoio. <br/> <br/>
                    A venda de bilhetes para sócios e estudantes apenas está disponível fisicamente, na Relíquia.

                </Text>
        </Flex>
        </Center>
        
        <Center>
            <Flex gap={"6px"}>
                {shows.map((show) => (
                <ShowCard key={show.id} show={show} />    
                ))} 
            </Flex>
        </Center>


            {isLoading && (
                <Flex justifyContent={"center"}>
                    <Spinner size={"xl"} />
                </Flex>
            )}

        </>
       
    );
}

export default Home;