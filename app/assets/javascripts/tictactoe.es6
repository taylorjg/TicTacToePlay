const CHOOSE_PIECE_MESSAGE = 'Choose noughts or crosses then click the Start button.';
const START_MESSAGE = 'Click the Start button to start a new game.';
const PLAYER1_TURN_MESSAGE = 'Your turn. Click an empty square to make your move.';
const PLAYER2_TURN_MESSAGE = 'The computer is thinking...';
const PLAYER1_WON_MESSAGE = 'You won!';
const PLAYER2_WON_MESSAGE = 'The computer won!';
const DRAW_MESSAGE = 'It\'s a draw!';
const UNKNOWN_WINNER_MESSAGE = 'I r confuse about who won!?';
const ARTIFICIAL_THINKING_TIME = 750;
const CROSS = 'X';
const NOUGHT = 'O';
const EMPTY = '-';

let player1Piece = CROSS;
let player2Piece = NOUGHT;
let started = false;
let gameOver = false;
let computerMoveInProgress = false;

$(document).ready(() => {
    $('#board td').click(onCellClick);
    $('#startBtn').click(onStart);
    reset();
});

function reset() {
    updateBoardFromString(EMPTY.repeat(9));
    $('#board td').removeClass('highlight');
    started = false;
    gameOver = false;
    computerMoveInProgress = false;
    hideSpinner();
}

function onStart() {
    reset();
    started = true;
}

function onCellClick(e) {
    if (!started || gameOver || computerMoveInProgress) {
        return;
    }
    const id = e.target.id;
    const ch = getCell(id);
    if (ch !== EMPTY) {
        return;
    }
    setCell(id, player1Piece);
    makeComputerMove();
}

function makeComputerMove() {

    computerMoveInProgress = true;

    setTimeout(() => {

        const requestData = {
            board: saveBoardToString(),
            player1Piece: player1Piece,
            player2Piece: player2Piece
        };

        $.post({
            url: '/api/computerMove',
            data: JSON.stringify(requestData),
            contentType: 'application/json'
        })
        .then(handleComputerMoveResponse)
        .catch((xhr, statusText, error) => { console.log(xhr, statusText, error); })
        .always(() => { computerMoveInProgress = false; });
    }, ARTIFICIAL_THINKING_TIME);
}

function handleComputerMoveResponse(state) {
    console.log(`state: ${state}`);
    console.dir(state);
    updateBoardFromString(state.board);
}

function setCell(id, piece) {
    $('#' + id).html(piece === CROSS || piece === NOUGHT ? piece : '');
}

function getCell(id) {
    var piece = $('#' + id).html();
    return piece === CROSS || piece === NOUGHT ? piece : EMPTY;
}

function highlightWinningLine(cellIndices) {
    var cellIndicesToIds = {
        0: 'cell00',
        1: 'cell01',
        2: 'cell02',
        3: 'cell10',
        4: 'cell11',
        5: 'cell12',
        6: 'cell20',
        7: 'cell21',
        8: 'cell22'
    };
    cellIndices.forEach(i => {
        const id = cellIndicesToIds[i];
        $('#' + id).addClass('highlight');
    });
}

function saveBoardToString() {
    return '' +
        getCell('cell00') +
        getCell('cell01') +
        getCell('cell02') +
        getCell('cell10') +
        getCell('cell11') +
        getCell('cell12') +
        getCell('cell20') +
        getCell('cell21') +
        getCell('cell22');
}

function updateBoardFromString(s) {
    setCell('cell00', s[0]);
    setCell('cell01', s[1]);
    setCell('cell02', s[2]);
    setCell('cell10', s[3]);
    setCell('cell11', s[4]);
    setCell('cell12', s[5]);
    setCell('cell20', s[6]);
    setCell('cell21', s[7]);
    setCell('cell22', s[8]);
}

function showSpinner() {
    $('#spinner').show();
}

function hideSpinner() {
    $('#spinner').hide();
}
