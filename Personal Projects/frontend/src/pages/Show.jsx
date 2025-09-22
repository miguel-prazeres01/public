import { Container, Stack, Text, Image, Flex, Button, Box, Heading } from "@chakra-ui/react";
import Navbar from "../components/Navbar";
import SeatGrid from "../components/SeatGrid";
import { useState } from "react";
import BlogContainer from "../components/BlogContainer";
import ReserveSeatsButton from "../components/ReserveSeatsButton";
import { useParams } from "react-router";


const Show = ({}) => {
    const [seats, setSeats] = useState([]);
    const [price, setPrice] = useState(0.0);
    const [reserved, setReserved] = useState([]);
    const [reservedServer, setReservedServer] = useState([]);
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const urlParams = useParams();
    const showId = urlParams.showId;

    return (
        <Stack minH={"100vh"}>
            <Navbar />
            <BlogContainer showId={showId}/>

            <Container maxW={'4xl'} p={5} shadow='md' borderWidth='1px'bg='gray.100'>
                <Heading align={'center'} 
                    fontWeight={"bold"}
                    letterSpacing={"1px"}
                    textAlign={"center"} 
                    fontSize={{ base: "2xl", md: "40" }}>
                        Palco</Heading>
            </Container>

            <Container maxW={"1200px"} my={4}>
                <Text
                    fontSize={{ base: "2xl", md: "40" }}
                    fontWeight={"bold"}
                    letterSpacing={"1px"}
                    textAlign={"center"}
                    mb={6}
                    mt={6}
                >
                    Plateia
                </Text>

                <SeatGrid showId={showId} seats={seats} setSeats={setSeats} setPrice={setPrice} reserved={reserved} setReserved={setReserved} reservedServer={reservedServer} setReservedServer={setReservedServer} refreshTrigger={refreshTrigger} spot='PLATEIA' />

                <Text
                    fontSize={{ base: "2xl", md: "40" }}
                    fontWeight={"bold"}
                    letterSpacing={"1px"}
                    textAlign={"center"}
                    mb={6}
                    mt={6}
                >
                    Balcão
                </Text>

                <SeatGrid showId={showId} seats={seats} setSeats={setSeats} setPrice={setPrice} reserved={reserved} setReserved={setReserved} reservedServer={reservedServer} setReservedServer={setReservedServer} refreshTrigger={refreshTrigger} spot='BALCAO' />
                <ReserveSeatsButton seats={seats} setSeats={setSeats} reserved={reserved} setReserved={setReserved} reservedServer={reservedServer} setReservedServer={setReservedServer} setRefreshTrigger={setRefreshTrigger} price={price} />

            </Container>
        </Stack>
    );
}

export default Show;