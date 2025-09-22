import {Button, Text, Card, CardBody, CardFooter, Stack, Divider, Heading, ButtonGroup, Image} from "@chakra-ui/react";
import {useState, useEffect} from "react";
import {SECRET} from "../App";
import { signPayload } from "../App";
import { BASE_URL } from "../App";
import { useNavigate } from "react-router-dom";
import { useParams } from "react-router";


const ShowCard = ({show}) => {

    const imageName = "/Cartaz" + show.id + ".jpg";
    const navigate = useNavigate();


    const navigateToShow = (showId) => {
        const navigateString = "/show/" + showId; 
        navigate(navigateString);
    };

    const formatDate =  () => {
        const date = new Date(show.dateTime);

        const formatted = date.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
        });

        return formatted;
    }


    return (
    
    <Card maxW='sm'>
                <CardBody>
                    <Image
                    src={imageName}
                    alt='Imagem espetáculo'
                    borderRadius='lg'
                    />
                    <Stack mt='6' spacing='3'>
                    <Heading size='md'>{show["name"]}</Heading>
                    <Text>
                        {formatDate()}
                    </Text>
                    <Text color='blue.600' fontSize='2xl'>
                        Bilhetes disponíveis na bilheteira
                    </Text>
                    </Stack>
                </CardBody>
                <Divider />
                <CardFooter>
                    <ButtonGroup spacing='2'>
                    <Button onClick={() => navigateToShow(show.id)} variant='solid' colorScheme='blue' isDisabled={true}>
                        Comprar bilhetes
                    </Button>
                    </ButtonGroup>
                </CardFooter>
            </Card>
    )

};

export default ShowCard;